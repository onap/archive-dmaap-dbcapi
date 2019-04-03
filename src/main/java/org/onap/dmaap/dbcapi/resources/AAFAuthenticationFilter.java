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
import org.onap.aaf.cadi.PropAccess;
import org.onap.aaf.cadi.filter.CadiFilter;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class AAFAuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AAFAuthenticationFilter.class.getName());
    private static final String CADI_PROPERTIES = "cadi.properties";
    private static final String AAF_AUTHN_FLAG = "UseAAF";

    private boolean isAafEnabled;
    private CadiFilter cadiFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        DmaapConfig dmaapConfig = (DmaapConfig) DmaapConfig.getConfig();
        String flag = dmaapConfig.getProperty(AAF_AUTHN_FLAG, "false");
        isAafEnabled = "true".equalsIgnoreCase(flag);
        initCadi(dmaapConfig);
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        if(isAafEnabled) {
            cadiFilter.doFilter(servletRequest, servletResponse, filterChain);
            updateResponseBody((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void updateResponseBody(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
        throws IOException {
        if(httpResponse.getStatus() == 401) {
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().print(buildErrorResponse("invalid or no credentials provided"));
            httpResponse.getWriter().flush();
        }
    }

    private String buildErrorResponse(String msg) {
        try {
            return new ObjectMapper().writeValueAsString(new ApiError(HttpStatus.UNAUTHORIZED_401, msg, "Authentication"));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Could not serialize response entity: " + e.getMessage());
            return "";
        }
    }


    @Override
    public void destroy() {
    }

    private void initCadi(DmaapConfig dmaapConfig) throws ServletException {
        if(isAafEnabled) {
            try {
                cadiFilter = new CadiFilter(new PropAccess(dmaapConfig.getProperty(CADI_PROPERTIES)));
            } catch (ServletException e) {
                LOGGER.error("CADI init error :" + e.getMessage());
                throw e;
            }
        }
    }

}
