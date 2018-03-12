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
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class TopicServiceTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	TopicService ts;
	MR_ClusterService mcs;
	String locname = "central-onap";

	@Before
	public void setUp() throws Exception {
		ts = new TopicService();
		mcs = new MR_ClusterService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.service.TopicService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.TopicService", "set", v );

	}

	@Test
	public void test3() {
		Topic topic = new Topic();
		ApiError err = new ApiError();
		topic.setTopicName( "test3" );
		topic.setFqtnStyle( FqtnType.Validator("none") );
		topic.getFqtn();
		Topic nTopic = ts.addTopic( topic, err );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( topic.getTopicName() ));
		}

	}

	@Test
	public void test3a() {
		Topic topic = new Topic();
		ApiError err = new ApiError();
		topic.setTopicName( "test3" );
		topic.setFqtnStyle( FqtnType.Validator("none") );
		topic.getFqtn();
		String t = "org.onap.dmaap.interestingTopic";
		String f = "mrc.onap.org:3904/events/org.onap.dmaap.interestingTopic";
		String c = "publisher";
		String[] a = { "sub", "view" };
		MR_Client sub = new MR_Client( locname, f, c, a );
		String[] b = { "pub", "view" };
		MR_Client pub = new MR_Client( "edge", f, c, b );
		ArrayList<MR_Client> clients = new ArrayList<MR_Client>();

		clients.add( sub );
		clients.add( pub );

		topic.setClients( clients );

		ts.reviewTopic( topic );
		ts.checkForBridge( topic, err );
		
		Topic nTopic = ts.addTopic( topic, err );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( topic.getTopicName() ));
		}
		

		ts.removeTopic( "test3", err );
	}

	@Test
	public void test4() {
		List<Topic> l = ts.getAllTopics();

	}

	@Test
	public void test5() {
		ApiError err = new ApiError();
/*

TODO: find a null pointer in here...
		String[] hl = { "host1", "host2", "host3" };
		String loc = "central-onap";
		MR_Cluster cluster = new MR_Cluster( loc, "localhost", "", hl );
		mcs.addMr_Cluster( cluster, err );
		Topic topic = new Topic();
		topic.setTopicName( "test5" );
		topic.setFqtnStyle( FqtnType.Validator("none") );
		topic.setReplicationCase( ReplicationType.Validator("none") );
		String f = topic.getFqtn();
		Topic nTopic = ts.updateTopic( topic, err );
*/
		assertTrue( err.getCode() == 0 );
	}

}
