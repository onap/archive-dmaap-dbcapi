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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FeedTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	String n, v, d, o, a;

	@Before
	public void setUp() throws Exception {
		n = "Chicken Feed";
		v = "1.0";
		d = "A daily helping of chicken eating metrics";
		o = "ab123";
		a = "Unrestricted";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.model.Feed", "get", null );	
	
	}

	@Test
	public void test2() {
		Feed t = new Feed( n, v, d, o, a );


		assertTrue( n.equals( t.getFeedName() ));
		assertTrue( v.equals( t.getFeedVersion() ));
		assertTrue( d.equals( t.getFeedDescription() ));
		assertTrue( o.equals( t.getOwner() ));
		assertTrue( a.equals( t.getAsprClassification() ) );
		assertTrue( ! t.isSuspended() );
	}

	@Test
	public void test3() {

		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.model.Feed", "set", v );
	}

}
