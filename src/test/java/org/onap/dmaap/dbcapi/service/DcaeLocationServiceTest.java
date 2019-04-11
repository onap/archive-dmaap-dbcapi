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
package org.onap.dmaap.dbcapi.service;

import org.junit.Test;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.DmaapObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DcaeLocationServiceTest {

    private DcaeLocationService locationService = new DcaeLocationService(new HashMap<>());

    @Test
    public void getAllDcaeLocations_shouldReturnEmptyCollection() {

        List<DcaeLocation> allDcaeLocations = locationService.getAllDcaeLocations();

        assertTrue(allDcaeLocations.isEmpty());
    }

    @Test
    public void addDcaeLocation_shouldAddLocationToMap() {
        DcaeLocation locationA = createDcaeLocation("locationA");

        DcaeLocation addedLocation = locationService.addDcaeLocation(locationA);

        assertEquals(locationA, locationService.getDcaeLocation("locationA"));
        assertSame(locationA, addedLocation);
    }

    @Test
    public void addDcaeLocation_shouldSetStatusAndLastModDate() {
        DcaeLocation locationA = createDcaeLocation("locationA");
        Date creationDate = new Date(10);
        locationA.setLastMod(creationDate);

        DcaeLocation addedLocation = locationService.addDcaeLocation(locationA);

        assertTrue(addedLocation.getLastMod().after(creationDate));
        assertEquals(DmaapObject.DmaapObject_Status.VALID, addedLocation.getStatus());
    }

    @Test
    public void updateDcaeLocation_shouldUpdateLocationAndLastModDate() {
        DcaeLocation location = createDcaeLocation("locationA");
        Date creationDate = new Date(10);
        location.setLastMod(creationDate);
        locationService.addDcaeLocation(location);

        DcaeLocation updatedLocation = locationService.updateDcaeLocation(location);

        assertTrue(updatedLocation.getLastMod().after(creationDate));
        assertSame(location, updatedLocation);
    }

    @Test
    public void updateDcaeLocation_shouldShouldReturnNullWhenLocationNameIsEmpty() {
        DcaeLocation location = createDcaeLocation("");

        DcaeLocation updatedLocation = locationService.updateDcaeLocation(location);

        assertNull(updatedLocation);
        assertTrue(locationService.getAllDcaeLocations().isEmpty());
    }

    @Test
    public void removeDcaeLocation_shouldRemoveLocationFromService() {
        locationService.addDcaeLocation(createDcaeLocation("locationA"));

        locationService.removeDcaeLocation("locationA");

        assertTrue(locationService.getAllDcaeLocations().isEmpty());
    }

    @Test
    public void getCentralLocation_shouldGetFirstCentralLocation() {
        locationService.addDcaeLocation(createDcaeLocation("locationA", "layerA"));
        locationService.addDcaeLocation(createDcaeLocation("locationB", "centralLayer"));

        assertEquals("locationB", locationService.getCentralLocation());
    }

    @Test
    public void getCentralLocation_shouldReturnDefaultCentralLocationNameWhenThereIsNoCentralLocation() {
        locationService.addDcaeLocation(createDcaeLocation("locationA", "layerA"));

        assertEquals("aCentralLocation", locationService.getCentralLocation());
    }

    @Test
    public void isEdgeLocation_shouldReturnTrueForNotCentralLocation() {
        locationService.addDcaeLocation(createDcaeLocation("locationA", "layerA"));
        locationService.addDcaeLocation(createDcaeLocation("locationB", "centralLayer"));

        assertTrue(locationService.isEdgeLocation("locationA"));
        assertFalse(locationService.isEdgeLocation("locationB"));
    }

    @Test
    public void isEdgeLocation_shouldReturnFalseWhenLocationDoesNotExist() {
        locationService.addDcaeLocation(createDcaeLocation("locationA", "layerA"));

        assertFalse(locationService.isEdgeLocation("not_existing_location"));
    }

    private DcaeLocation createDcaeLocation(String locationName) {
        return createDcaeLocation(locationName, "dcaeLayer");
    }

    private DcaeLocation createDcaeLocation(String locationName, String dcaeLayer) {
        return new DcaeLocation("clli", dcaeLayer, locationName, "openStackAvailabilityZone", "subnet");
    }


}
