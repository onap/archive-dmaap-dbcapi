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

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class DmaapResourceTest {

    private static final DmaapObjectFactory DMAAP_OBJECT_FACTORY = new DmaapObjectFactory();
    private static final String PROVIDER_URL = "http://provider.url";
    private static final String DMAAP_NAME = "name";
    private static final String TOPICS_ROOT = "topics_root";
    private static final String BRIDGE_ADMIN_TOPIC = "bridge_admin_topic";
    private static FastJerseyTestContainer testContainer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DatabaseClass.getDmaap().init(DMAAP_OBJECT_FACTORY.genDmaap());

        testContainer = new FastJerseyTestContainer(new ResourceConfig()
                .register(DmaapResource.class));
        testContainer.init();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        testContainer.destroy();
        DatabaseClass.clearDatabase();
    }

    @Before
    public void cleanupDatabase() {
        DatabaseClass.clearDatabase();
    }

    @Test
    public void getDmaap_shouldReturnDmaapDetails() {

        Response response = testContainer.target("dmaap").request().get(Response.class);

        assertEquals(200, response.getStatus());
        assertNotNull(response.readEntity(Dmaap.class));
    }

    @Test
    public void addDmaap_shouldCreateNewDmaap() {
        Dmaap dmaap = createDmaap(DMAAP_NAME, PROVIDER_URL, TOPICS_ROOT, BRIDGE_ADMIN_TOPIC);

        Response response = addDmaap(entity(dmaap, APPLICATION_JSON));

        assertEquals(200, response.getStatus());
        assertEquals(dmaap, response.readEntity(Dmaap.class));
    }

    @Test
    public void addDmaap_shouldThrowErrorWhenRequiredFieldIsMissing() {
        Dmaap dmaap = createDmaap(DMAAP_NAME, null, TOPICS_ROOT, BRIDGE_ADMIN_TOPIC);

        Response response = addDmaap(entity(dmaap, APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("dmaapProvUrl", responseError.getFields());
    }

    @Test
    public void updateDmaap_shouldUpdateDmaapInstace() {
        Dmaap dmaap = createDmaap(DMAAP_NAME, PROVIDER_URL, TOPICS_ROOT, BRIDGE_ADMIN_TOPIC);

        addDmaap(entity(dmaap, APPLICATION_JSON));

        Dmaap updatedDmaap = createDmaap(DMAAP_NAME, "http://new.provider.url", TOPICS_ROOT, BRIDGE_ADMIN_TOPIC);
        Response response = testContainer.target("dmaap")
                .request()
                .put(entity(updatedDmaap, APPLICATION_JSON), Response.class);

        assertEquals(200, response.getStatus());
        Dmaap actual = response.readEntity(Dmaap.class);
        assertEquals(updatedDmaap, actual);
    }

    private Dmaap createDmaap(String dmaapName, String providerUrl, String topicsNsRoot, String bridgeAdminTopic) {
        return new Dmaap("1.0", topicsNsRoot, dmaapName, providerUrl, "", bridgeAdminTopic, "", "");
    }

    private Response addDmaap(Entity<Dmaap> requestedEntity) {
        return testContainer.target("dmaap")
                .request()
                .post(requestedEntity, Response.class);
    }
}

