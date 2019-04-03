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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.service.DmaapService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import sun.security.acl.PrincipalImpl;

@RunWith(MockitoJUnitRunner.class)
public class AAFAuthorizationFilterTest {

    private static final String DMAAP_NAME = "mr";
    @Spy
    private AAFAuthorizationFilter filter;
    @Mock
    private FilterConfig filterConfig;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private DmaapConfig dmaapConfig;
    @Mock
    private DmaapService dmaapService;

    @Before
    public void setUp() throws Exception {
        doReturn(dmaapConfig).when(filter).getConfig();
        doReturn(dmaapService).when(filter).getDmaapService();
        when(dmaapConfig.getProperty(AAFAuthorizationFilter.API_NS_PROP, AAFAuthorizationFilter.DEFAULT_API_NS))
            .thenReturn(AAFAuthorizationFilter.DEFAULT_API_NS);
    }

    @Test
    public void init_shouldNotInitializeDmaapInstance_whenAAFnotUsed() throws Exception {
        //given
        configureAAFUsage(false);

        //when
        filter.init(filterConfig);

        //then
        verifyZeroInteractions(dmaapService);
        assertNull(filter.getInstance());
    }

    @Test
    public void init_shouldInitializeDmaapInstance_whenAAFisUsed() throws Exception {
        //given
        configureAAFUsage(true);
        doReturn(provideEmptyInstance()).when(dmaapService).getDmaap();

        //when
        filter.init(filterConfig);

        //then
        verify(dmaapService).getDmaap();
        assertNotNull(filter.getInstance());
    }

    @Test
    public void doFilter_shouldSkipAuthorization_whenAAFnotUsed() throws Exception {
        //given
        configureAAFUsage(false);

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(filterChain).doFilter(servletRequest,servletResponse);
        verifyNoMoreInteractions(filterChain);
        verifyZeroInteractions(dmaapService, servletRequest, servletResponse);
    }

    @Test
    public void doFilter_shouldUpdateDmaapInstance_whenAAFisUsed_andNoCurrentInstance() throws Exception {
        //given
        configureAAFUsage(true);
        when(dmaapService.getDmaap()).thenReturn(provideEmptyInstance());
        initFilterForSuccessResponse();

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(dmaapService, times(2)).getDmaap();
        assertEquals(AAFAuthorizationFilter.BOOT_INSTANCE, filter.getInstance());
    }

    @Test
    public void doFilter_shouldUpdateDmaapInstance_whenAAFisUsed_andBootInstance() throws Exception {
        //given
        configureAAFUsage(true);
        when(dmaapService.getDmaap()).thenReturn(provideEmptyInstance(), provideEmptyInstance(), provideRealInstance());
        initFilterForSuccessResponse();
        //given boot instance
        filter.doFilter(servletRequest,servletResponse,filterChain);
        assertEquals(AAFAuthorizationFilter.BOOT_INSTANCE, filter.getInstance());

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(dmaapService, times(3)).getDmaap();
        assertEquals(DMAAP_NAME, filter.getInstance());
    }

    @Test
    public void doFilter_shouldNotUpdateDmaapInstance_whenAAFisUsed_andAlreadyRealInstance() throws Exception {
        //given
        configureAAFUsage(true);
        when(dmaapService.getDmaap()).thenReturn(provideRealInstance());
        initFilterForSuccessResponse();

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(dmaapService).getDmaap();
        assertEquals(DMAAP_NAME, filter.getInstance());
    }

    @Test
    public void doFilter_shouldPass_whenUserHasPermissionToResourceEndpoint() throws Exception {
        //given
        String user = "johnny";
        String path = "/topics/";
        String pemType = "topics";
        String method = "GET";
        configureFilterForPermissionCheck(true, user, path, pemType, method);

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(filterChain).doFilter(servletRequest,servletResponse);
        verifyZeroInteractions(servletResponse);
    }

    @Test
    public void doFilter_shouldReturnError_whenUserDontHavePermissionToResourceEndpoint() throws Exception {
        //given
        String user = "jack";
        String path = "/subpath/topics";
        String pemType = "topics";
        String method = "GET";
        configureFilterForPermissionCheck(false, user, path, pemType, method);

        String errorMsgJson = "{\"code\":403,\"message\":\"User "+user+" does not have permission "
            + buildPermission(pemType, DMAAP_NAME, method) +"\",\"fields\":\"Authorization\",\"2xx\":false}";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(servletResponse.getWriter()).thenReturn(pw);

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verifyZeroInteractions(filterChain);
        verify(servletResponse).setStatus(403);
        assertEquals(errorMsgJson, sw.toString());
    }

    private void configureFilterForPermissionCheck(boolean isUserInRole, String user, String path, String pemType, String method) throws Exception {
        configureAAFUsage(true);
        when(dmaapService.getDmaap()).thenReturn(provideRealInstance());
        doReturn(path).when(servletRequest).getPathInfo();
        doReturn(method).when(servletRequest).getMethod();
        when(servletRequest.getUserPrincipal()).thenReturn(new PrincipalImpl(user));
        when(servletRequest.isUserInRole(buildPermission(pemType, DMAAP_NAME, method))).thenReturn(isUserInRole);
        filter.init(filterConfig);
    }

    private String buildPermission(String type, String instance, String action) {
        StringBuilder sb = new StringBuilder(AAFAuthorizationFilter.DEFAULT_API_NS);
        sb.append(".").append(type).append("|").append(instance).append("|").append(action);
        return sb.toString();
    }

    private void initFilterForSuccessResponse() throws Exception{
        doReturn("/dmaap").when(servletRequest).getPathInfo();
        doReturn("GET").when(servletRequest).getMethod();
        when(servletRequest.isUserInRole(anyString())).thenReturn(true);
        when(servletRequest.getUserPrincipal()).thenReturn(new PrincipalImpl("username"));
        filter.init(filterConfig);
    }

    private Dmaap provideEmptyInstance() {
        return new  Dmaap("0", "", "", "", "", "", "", "");
    }

    private Dmaap provideRealInstance() {
        Dmaap dmaap = new Dmaap("1", "org.onap.dmaap", DMAAP_NAME, "https://dmaap-dr-prov:8443", "", "DCAE_MM_AGENT", "", "");
        dmaap.setStatus(DmaapObject_Status.VALID);
        return dmaap;
    }

    private void configureAAFUsage(Boolean isUsed) {
        doReturn(isUsed.toString()).when(dmaapConfig).getProperty(eq(AAFAuthorizationFilter.AAF_AUTHZ_FLAG), anyString());
    }
}