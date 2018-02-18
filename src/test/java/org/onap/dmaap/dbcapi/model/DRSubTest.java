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
public class DRSubTest {
	String d, un, up, f, du, lu, s, o;
	Boolean u100, susp;

	@Before
	public void setUp() throws Exception {
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";
		f = "22";
		s = "sub123";
		du = "sub.server.onap.org:8443/deliver/here";
		lu = "https://drps.onap.org:8443/sublog/123";
		u100 = true;
		susp = false;
		o = "joe";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDRSubClassDefaultConstructor() {

		DR_Sub t = new DR_Sub();
	
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getUsername() == null  );
		assertTrue( t.getUserpwd() == null  );
		assertTrue( t.getFeedId() == null  );
		assertTrue( t.getDeliveryURL() == null  );
		assertTrue( t.getLogURL() == null  );
		assertTrue( t.getSubId() == null  );
		assertTrue( ! t.isUse100() );
		assertTrue( ! t.isSuspended() );
		assertTrue( t.getOwner() == null  );
	
	}

	@Test
	public void testDRSubClassConstructor() {

		DR_Sub t = new DR_Sub( d, un, up, f, du, lu, u100 );
	
		assertTrue( d.equals( t.getDcaeLocationName() ));
		assertTrue( un.equals( t.getUsername() ));
		assertTrue( up.equals( t.getUserpwd() ));
		assertTrue( f.equals( t.getFeedId() ));
		assertTrue( du.equals( t.getDeliveryURL() ) );
		assertTrue( lu.equals( t.getLogURL() ) );
		assertTrue( t.isUse100() );
		assertTrue( ! t.isSuspended() );
	}

	@Test
	public void testDRSubClassSetters() {

		DR_Sub t = new DR_Sub();

		t.setDcaeLocationName( d );
		assertTrue( d.equals( t.getDcaeLocationName() ));
		t.setUsername( un );
		assertTrue( un.equals( t.getUsername() ));
		t.setUserpwd( up );
		assertTrue( up.equals( t.getUserpwd() ));
		t.setFeedId( f );
		assertTrue( f.equals( t.getFeedId() ));
		t.setSubId( s );
		assertTrue( s.equals( t.getSubId() ));
		t.setDeliveryURL( du );
		assertTrue( du.equals( t.getDeliveryURL() ) );
		t.setLogURL( lu );
		assertTrue( lu.equals( t.getLogURL() ) );
	
	}

	@Test
	public void test3() {
		String json = String.format( "{ \"%s\": \"%s\", \"%s\": false, \"%s\": { \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": \"%s\"  },  \"%s\": { \"%s\": \"%s\",  \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": true }  }",
				"subscriber", "owner",
				"suspend", 
				"links",
					"feed", "https://feed.onap.org/publish/22",
					"self", "https://feed.onap.org/subscriber/44",
					"log" , lu,
					"delivery" , 
					"url", du,
					"user", un,
					"password", up,
					"use100"
				);


		DR_Sub t = new DR_Sub( json );

		assertTrue( un.equals( t.getUsername() ));
		assertTrue( up.equals( t.getUserpwd() ));
		assertTrue( f.equals( t.getFeedId() ));
		assertTrue( du.equals( t.getDeliveryURL() ) );
		assertTrue( lu.equals( t.getLogURL() ) );
		assertTrue( ! t.isSuspended() );

		String o = t.toString();

	}
}
