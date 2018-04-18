/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dbcapi.model;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;
public class MR_ClusterTest {
	String d, fqdn;

	ReflectionHarness rh = new ReflectionHarness();

	@Before
	public void setUp() throws Exception {
		d = "central-onap";
		fqdn = "mr.onap.org";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testMR_ClusterClassDefaultConstructor() {

		MR_Cluster t = new MR_Cluster();
	
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getFqdn() == null  );
	
	}

	@Test
	public void testMR_ClusterClassConstructor() {

		MR_Cluster t = new MR_Cluster( d, fqdn, "http", "3904");

		t.getHosts();
	
		assertTrue( t.getDcaeLocationName() == d  );
		assertTrue( t.getFqdn() == fqdn  );
	}

	@Test
	public void testw3() {

		MR_Cluster t = new MR_Cluster();
		String[] h = { "host1", "host2", "host3" };
		t.setHosts( h );
	
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getFqdn() == null  );

		String fqtn = t.genTopicURL( "cluster2.onap.org", "org.onap.topic2" );	
	}



	@Test
	public void testsetter() {

		String v = "validate";


		rh.reflect( "org.onap.dmaap.dbcapi.model.MR_Cluster", "set", v );	
	
	}

}
