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
package org.onap.dmaap.dbcapi.service;
import  org.onap.dmaap.dbcapi.model.*;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class DR_NodeServiceTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	DR_NodeService ns;

	@Before
	public void setUp() throws Exception {
		ns = new DR_NodeService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.service.DR_NodeService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.DR_NodeService", "set", v );

	}

	@Test
	public void test3() {
		String f = "drsn01.onap.org";
		String locname = "central-demo";

		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation loc = new DcaeLocation( "CLLI1234", "central-onap", locname, "aZone", "10.10.10.0/24" );
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		DR_Node node = new DR_Node( f, locname, "zplvm009.onap.org", "1.0.46" );
		DR_Node n2 = ns.addDr_Node( node, err );	

		if ( n2 != null ) {
			n2 = ns.getDr_Node( f,  err );
		}

		List<DR_Node> l = ns.getAllDr_Nodes();
		if ( n2 != null ) {
			n2.setVersion( "1.0.47" );
			n2 = ns.updateDr_Node( n2, err );
		}

		n2 = ns.removeDr_Node( f,  err );
				

	}

/*
	@Test
	public void test4() {
		List<MR_Client> l = cls.getAllMr_Clients();

		ArrayList<MR_Client> al = cls.getAllMrClients( "foo" );

		ArrayList<MR_Client> al2 = cls.getClientsByLocation( "central" );
	}

	@Test
	public void test5() {
		Topic topic = new Topic();
		ApiError err = new ApiError();
		topic.setTopicName( "test3" );
		topic.setFqtnStyle( FqtnType.Validator("none") );
		topic.getFqtn();
		Topic nTopic = ts.addTopic( topic, err );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( topic.getTopicName() ));
		}
		String[] actions = { "pub", "view" };
		MR_Client c = new MR_Client( "central-onap", "org.onap.dmaap.demo.interestingTopic2", "org.onap.clientApp.publisher", actions );

		c = cls.addMr_Client( c, topic, err );
		if ( c != null ) {
				actions[0] = "sub";
				c.setAction( actions );
				c = cls.updateMr_Client( c, err );
				assertTrue( err.getCode() == 200 );
		}
	}
*/

}
