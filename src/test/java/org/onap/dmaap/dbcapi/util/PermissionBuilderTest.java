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
package org.onap.dmaap.dbcapi.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.service.DmaapService;

@RunWith(MockitoJUnitRunner.class)
public class PermissionBuilderTest {

    private static final String DMAAP_NAME = "mr";
    private PermissionBuilder permissionBuilder;
    @Mock
    private DmaapConfig dmaapConfig;
    @Mock
    private DmaapService dmaapService;
    @Mock
    private HttpServletRequest request;


    @Test
    public void updateDmaapInstance_shouldSetBootInstance_whenDmaapIsNotInitialized() {
        //given
        doReturn(null).when(dmaapService).getDmaap();
        permissionBuilder = new PermissionBuilder(dmaapConfig, dmaapService);

        //when
        permissionBuilder.updateDmaapInstance();

        //then
        assertEquals(PermissionBuilder.BOOT_INSTANCE, permissionBuilder.getInstance());
    }

    @Test
    public void updateDmaapInstance_shouldSetBootInstance_whenDmaapIsInitializedWithDefaultInstance() {
        //given
        doReturn(provideDefaultInstance()).when(dmaapService).getDmaap();
        permissionBuilder = new PermissionBuilder(dmaapConfig, dmaapService);

        //when
        permissionBuilder.updateDmaapInstance();

        //then
        assertEquals(PermissionBuilder.BOOT_INSTANCE, permissionBuilder.getInstance());
    }

    @Test
    public void updateDmaapInstance_shouldSetRealInstance_whenDmaapServiceProvidesOne() {
        //given
        when(dmaapService.getDmaap()).thenReturn(provideDefaultInstance(), provideRealInstance(DMAAP_NAME));
        permissionBuilder = new PermissionBuilder(dmaapConfig, dmaapService);

        //when
        permissionBuilder.updateDmaapInstance();

        //then
        assertEquals(DMAAP_NAME, permissionBuilder.getInstance());
    }

    @Test
    public void updateDmaapInstance_shouldNotUpdateDmaapInstance_whenAlreadyInitializedWithRealInstance() {
        //given
        when(dmaapService.getDmaap()).thenReturn(provideRealInstance(DMAAP_NAME), provideRealInstance("newName"));
        permissionBuilder = new PermissionBuilder(dmaapConfig, dmaapService);

        //when
        permissionBuilder.updateDmaapInstance();

        //then
        assertEquals(DMAAP_NAME, permissionBuilder.getInstance());
        verify(dmaapService, atMost(1)).getDmaap();
    }

    @Test
    public void buildPermission_shouldBuildPermissionWithBootInstance() {
        //given
        String path = "/dmaap";
        String method = "GET";
        initPermissionBuilder(path, method, provideDefaultInstance());

        //when
        String permission = permissionBuilder.buildPermission(request);

        //then
        assertEquals("org.onap.dmaap-bc.api.dmaap|boot|GET", permission);
    }

    @Test
    public void buildPermission_shouldBuildPermissionWithRealInstance() {
        //given
        String path = "/subpath/topics/";
        String method = "GET";
        initPermissionBuilder(path, method, provideRealInstance(DMAAP_NAME));

        //when
        String permission = permissionBuilder.buildPermission(request);

        //then
        assertEquals("org.onap.dmaap-bc.api.topics|mr|GET", permission);
    }

    private void initPermissionBuilder(String path, String method, Dmaap dmaapInstance) {
        when(dmaapConfig.getProperty(PermissionBuilder.API_NS_PROP, PermissionBuilder.DEFAULT_API_NS))
            .thenReturn(PermissionBuilder.DEFAULT_API_NS);
        when(dmaapService.getDmaap()).thenReturn(dmaapInstance);
        permissionBuilder = new PermissionBuilder(dmaapConfig, dmaapService);

        when(request.getPathInfo()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);
    }

    private Dmaap provideDefaultInstance() {
        return new  Dmaap("0", "", "", "", "", "", "", "");
    }

    private Dmaap provideRealInstance(String dmaapName) {
        Dmaap dmaap = new Dmaap("1", "org.onap.dmaap", dmaapName, "https://dmaap-dr-prov:8443", "", "DCAE_MM_AGENT", "", "");
        dmaap.setStatus(DmaapObject_Status.VALID);
        return dmaap;
    }

}