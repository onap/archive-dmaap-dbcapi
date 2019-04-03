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

package org.onap.dmaap.dbcapi.resources;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.apache.log4j.Logger;
import org.onap.dmaap.dbcapi.authentication.AuthenticationErrorException;
import org.onap.dmaap.dbcapi.service.ApiService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;


@Authorization
public class AuthorizationFilter implements ContainerRequestFilter   {

	private static final String AAF_FLAG = "UseAAF";
	private final Logger logger = Logger.getLogger(AuthorizationFilter.class.getName());
	private final ResponseBuilder responseBuilder = new ResponseBuilder();
	private final boolean isAafEnabled;


	public AuthorizationFilter() {
		DmaapConfig dmaapConfig = (DmaapConfig) DmaapConfig.getConfig();
		String flag = dmaapConfig.getProperty(AAF_FLAG, "false");
		isAafEnabled = "true".equalsIgnoreCase(flag);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {

		if(!isAafEnabled) {
			ApiService apiResp = new ApiService()
				.setAuth(requestContext.getHeaderString("Authorization"))
				.setUriPath(requestContext.getUriInfo().getPath())
				.setHttpMethod(requestContext.getMethod())
				.setRequestId(requestContext.getHeaderString("X-ECOMP-RequestID"));

			try {
				apiResp.checkAuthorization();
			} catch (AuthenticationErrorException ae) {
				logger.error("Error", ae);
				requestContext.abortWith(responseBuilder.unauthorized(apiResp.getErr().getMessage()));
			} catch (Exception e) {
				logger.error("Error", e);
				requestContext.abortWith(responseBuilder.unavailable());
			}
		}
	}

}
