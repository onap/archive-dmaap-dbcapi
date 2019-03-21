/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dmaap.dbcapi.service;

import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;
import static com.att.eelf.configuration.Configuration.MDC_PARTNER_NAME;
import static com.att.eelf.configuration.Configuration.MDC_SERVICE_NAME;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.authentication.ApiPolicy;
import org.onap.dmaap.dbcapi.authentication.AuthenticationErrorException;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.resources.RequiredFieldException;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.RandomString;
import org.slf4j.MDC;

public class ApiService extends BaseLoggingClass {

    private String apiNamespace;
    private String uri;
    private String uriPath;
    private String method;
    private String authorization;
    private String requestId;
    private ApiError err;
    private ApiPolicy apiPolicy;

    public ApiService() {

        err = new ApiError();
        requestId = (new RandomString(10)).nextString();

        if (apiNamespace == null) {
            DmaapConfig p = (DmaapConfig) DmaapConfig.getConfig();
            apiNamespace = p.getProperty("ApiNamespace", "org.openecomp.dmaapBC.api");
            logger.info("config param usePE has been deprecated.  Use ApiPermission.Class property instead.");
        }
        apiPolicy = new ApiPolicy();

        logger.info("apiNamespace=" + apiNamespace);
    }

    public ApiService setAuth(String auth) {
        this.authorization = auth;
        logger.info("setAuth:  authorization={} ", authorization);
        return this;
    }

    private void setServiceName() {
        String svcRequest = new String(this.method + " " + this.uriPath);
        MDC.put(MDC_SERVICE_NAME, svcRequest);
    }

    public ApiService setHttpMethod(String httpMethod) {
        this.method = httpMethod;
        logger.info("setHttpMethod: method={} ", method);
        setServiceName();
        return this;
    }

    public ApiService setUriPath(String uriPath) {
        this.uriPath = uriPath;
        this.uri = setUriFromPath(uriPath);
        logger.info("setUriPath: uriPath={} uri={}", uriPath, uri);
        setServiceName();
        return this;
    }

    private String setUriFromPath(String uriPath) {
        int ch = uriPath.indexOf("/");
        if (ch > 0) {
            return ((String) uriPath.subSequence(0, ch));
        } else {
            return uriPath;
        }
    }

    public ApiError getErr() {
        return err;
    }


    public void setErr(ApiError err) {
        this.err = err;
    }


    // test for presence of a required field
    public void required(String name, Object val, String expr) throws RequiredFieldException {
        err.setCode(0);
        if (val == null) {
            err.setCode(Status.BAD_REQUEST.getStatusCode());
            err.setMessage("missing required field");
            err.setFields(name);
            throw new RequiredFieldException();
        }
        if (expr != null && !expr.isEmpty()) {
            Pattern pattern = Pattern.compile(expr);
            Matcher matcher = pattern.matcher((CharSequence) val);
            if (!matcher.find()) {
                err.setCode(Status.BAD_REQUEST.getStatusCode());
                err.setMessage("value '" + val + "' violates regexp check '" + expr + "'");
                err.setFields(name);
                throw new RequiredFieldException();
            }
        }
    }

    // utility to serialize ApiErr object
    public String toString() {
        return String.format("code=%d msg=%s fields=%s", err.getCode(), err.getMessage(), err.getFields());
    }


    public void setCode(int statusCode) {
        err.setCode(statusCode);
    }


    public void setMessage(String string) {
        err.setMessage(string);
    }


    public void setFields(String string) {
        err.setFields(string);
    }

    public void checkAuthorization(String auth, String uriPath, String httpMethod)
        throws AuthenticationErrorException, Exception {
        authorization = auth;
        setUriFromPath(uriPath);
        method = httpMethod;

        checkAuthorization();
    }


    public void checkAuthorization() throws AuthenticationErrorException, Exception {

        MDC.put(MDC_KEY_REQUEST_ID, requestId);

        logger.info("request: uri={} method={} auth={}", uri, method, authorization);

        if (uri == null || uri.isEmpty()) {
            String errmsg = "No URI value provided ";
            err.setMessage(errmsg);
            logger.info(errmsg);
            throw new AuthenticationErrorException();
        }
        if (method == null || method.isEmpty()) {
            String errmsg = "No method value provided ";
            err.setMessage(errmsg);
            logger.info(errmsg);
            throw new AuthenticationErrorException();
        }
        DmaapService dmaapService = new DmaapService();
        Dmaap dmaap = dmaapService.getDmaap();
        String env = dmaap.getDmaapName();

        // special case during bootstrap of app when DMaaP environment may not be set.
        // this allows us to authorize certain APIs used for initialization during this window.
        if (env == null || env.isEmpty()) {
            env = "boot";
        }
        if (!apiPolicy.getUseAuthClass()) {
            return;  // skip authorization if not enabled
        }
        if (authorization == null || authorization.isEmpty()) {
            String errmsg = "No basic authorization value provided ";
            err.setMessage(errmsg);
            logger.info(errmsg);
            throw new AuthenticationErrorException();
        }
        String credentials = authorization.substring("Basic".length()).trim();
        byte[] decoded = DatatypeConverter.parseBase64Binary(credentials);
        String decodedString = new String(decoded);
        String[] actualCredentials = decodedString.split(":");
        String ID = actualCredentials[0];
        String Password = actualCredentials[1];
        MDC.put(MDC_PARTNER_NAME, ID);
        try {

            DmaapPerm p = new DmaapPerm(apiNamespace + "." + uri, env, method);
            apiPolicy.check(ID, Password, p);
        } catch (AuthenticationErrorException ae) {
            String errmsg =
                "User " + ID + " failed authentication/authorization for " + apiNamespace + "." + uriPath + " " + env
                    + " " + method;
            logger.info(errmsg);
            err.setMessage(errmsg);
            throw ae;

        }


    }

    public String getRequestId() {
        return requestId;
    }

    public ApiService setRequestId(String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            this.requestId = (new RandomString(10)).nextString();
            logger.warn("X-ECOMP-RequestID not set in HTTP Header.  Setting RequestId value to: " + this.requestId);
        } else {
            this.requestId = requestId;
        }
        MDC.put(MDC_KEY_REQUEST_ID, this.requestId);
        return this;
    }
}
