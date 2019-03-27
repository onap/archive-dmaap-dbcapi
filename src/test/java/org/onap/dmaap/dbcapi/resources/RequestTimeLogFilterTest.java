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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.att.eelf.configuration.EELFLogger;
import java.time.Clock;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RequestTimeLogFilterTest {

    private ContainerRequestContext requestContext;
    private Clock clock;
    private EELFLogger logger;

    private RequestTimeLogFilter requestTimeLogFilter;

    @Before
    public void setup() {
        requestContext = mock(ContainerRequestContext.class);
        logger = mock(EELFLogger.class);
        clock = mock(Clock.class);
        requestTimeLogFilter = new RequestTimeLogFilter(logger, clock);
    }

    @Test
    public void filterShouldStartTimestampProperty() {
        requestTimeLogFilter.filter(requestContext);

        verify(requestContext).setProperty(eq("start"), anyLong());
        verify(clock).millis();
    }

    @Test
    public void filterShouldReturnElapsedTime() {
        long startTimestamp = 1L;
        long stopTimestamp = 2L;
        when(requestContext.getProperty(eq("start"))).thenReturn(startTimestamp);
        when(clock.millis()).thenReturn(stopTimestamp);

        requestTimeLogFilter.filter(requestContext, mock(ContainerResponseContext.class));

        verify(requestContext).getProperty(eq("start"));
        verify(logger).info(anyString(), eq(stopTimestamp - startTimestamp));
    }
}