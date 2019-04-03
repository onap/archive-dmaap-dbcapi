/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dbcapi.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.service.DmaapService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class AAFAuthorizationFilter implements Filter{

    private static final Logger LOGGER = Logger.getLogger(AAFAuthenticationFilter.class.getName());
    private static final String PERM_SEPARATOR = "|";
    private static final String NS_SEPARATOR = ".";
    static final String AAF_AUTHZ_FLAG = "UseAAF";
    static final String API_NS_PROP = "ApiNamespace";
    static final String DEFAULT_API_NS = "org.onap.dmaap-bc.api";
    static final String BOOT_INSTANCE = "boot";
    private String apiNamespace;
    private String instance;
    private boolean isAafEnabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        DmaapConfig dmaapConfig = getConfig();
        apiNamespace = dmaapConfig.getProperty(API_NS_PROP, DEFAULT_API_NS);
        isAafEnabled = "true".equalsIgnoreCase(dmaapConfig.getProperty(AAF_AUTHZ_FLAG, "false"));
        if(isAafEnabled) {
            instance = getDmaapName();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        if(isAafEnabled) {
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            updateDmaapInstance();
            String permission = buildPermission(httpRequest);

            if (httpRequest.isUserInRole(permission)) {
                LOGGER.info("User " + httpRequest.getUserPrincipal().getName() + " has permission " + permission);
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                String msg = "User " + httpRequest.getUserPrincipal().getName() + " does not have permission " + permission;
                LOGGER.error(msg);
                ((HttpServletResponse) servletResponse).setStatus(HttpStatus.FORBIDDEN_403);
                servletResponse.setContentType("application/json");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().print(buildErrorResponse(msg));
                servletResponse.getWriter().flush();
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        //nothing to cleanup
    }

    DmaapService getDmaapService() {
        return new DmaapService();
    }

    DmaapConfig getConfig() {
        return (DmaapConfig) DmaapConfig.getConfig();
    }

    String getInstance() {
        return instance;
    }

    private String getDmaapName() {
        return getDmaapService().getDmaap().getDmaapName();
    }

    private String buildErrorResponse(String msg) {
        try {
            return new ObjectMapper().writeValueAsString(new ApiError(HttpStatus.FORBIDDEN_403, msg, "Authorization"));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Could not serialize response entity: " + e.getMessage());
            return "";
        }
    }

    private synchronized void updateDmaapInstance() {
        if(instance == null || instance.isEmpty() || instance.equalsIgnoreCase(BOOT_INSTANCE)) {
            String dmaapName = getDmaapName();
            instance = (dmaapName == null || dmaapName.isEmpty()) ? BOOT_INSTANCE : dmaapName;
        }
    }

    private String buildPermission(HttpServletRequest httpRequest) {

        StringBuilder sb = new StringBuilder(apiNamespace);
        sb.append(NS_SEPARATOR)
            .append(getPermissionType(httpRequest.getPathInfo()))
            .append(PERM_SEPARATOR)
            .append(instance)
            .append(PERM_SEPARATOR)
            .append(httpRequest.getMethod());
        return sb.toString();
    }

    private String getPermissionType(String pathInfo) {
        char pathSeparator = '/';
        String relativePath = (pathInfo.charAt(pathInfo.length()-1) == pathSeparator) ?
            pathInfo.substring(0,pathInfo.length()-1) : pathInfo;

        String[] pathSlices = relativePath.split(String.valueOf(pathSeparator));
        return pathSlices[pathSlices.length-1];
    }
}
