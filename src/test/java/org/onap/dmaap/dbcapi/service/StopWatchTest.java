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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;

public class StopWatchTest extends BaseLoggingClass {

    private StopWatch stopWatch = new StopWatch();

    @Test
    public void stopWatchShouldReturnElapsedTime() throws InterruptedException {
        stopWatch.start();
        Thread.sleep(50);
        stopWatch.stop();
        assertTrue(stopWatch.getElapsedTime() > 0);
    }

    @Test
    public void resetShouldSetElapsedTime() {
        stopWatch.start();
        stopWatch.stop();
        stopWatch.resetElapsedTime();
        assertEquals(0,stopWatch.getElapsedTime());

    }

}