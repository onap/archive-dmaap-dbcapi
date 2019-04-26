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

import org.junit.BeforeClass;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import  org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class TopicServiceTest {

	private static final String  fmt = "%24s: %s%n";
	private static DmaapObjectFactory factory = new DmaapObjectFactory();
	ReflectionHarness rh = new ReflectionHarness();

	private TopicService ts;
	private MR_ClusterService mcs;
	private MR_ClientService cls;
	private DcaeLocationService dls;

	String locname;

	@BeforeClass
	public static void setUpClass() {
		DatabaseClass.getDmaap().init(factory.genDmaap());
	}

	@Before
	public void setUp() throws Exception {
		ts = new TopicService();
		assert( ts != null );
		mcs = new MR_ClusterService();
		assert( mcs != null );
		ts = new TopicService();
		mcs = new MR_ClusterService();
		cls = new MR_ClientService();

		dls = new DcaeLocationService();
		DcaeLocation loc = factory.genDcaeLocation( "central" );
		locname = loc.getDcaeLocationName();
		dls.addDcaeLocation( loc );
		loc = factory.genDcaeLocation( "edge");

		ApiError err = new ApiError();
		
		MR_Cluster node = factory.genMR_Cluster( "central" );
		mcs.addMr_Cluster( node, err);
		node = factory.genMR_Cluster("edge" );
		mcs.addMr_Cluster(node,  err);
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
		String t = "test3";
		Topic topic = factory.genSimpleTopic( t );
		ApiError err = new ApiError();
		
		Topic nTopic = ts.addTopic( topic, err, false );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( t ));
		}

	}

	@Test
	public void test3a() {


		ApiError err = new ApiError();

		String t = "org.onap.dmaap.interestingTopic";
		Topic topic = factory.genSimpleTopic(t);
		String f = "mrc.onap.org:3904/events/org.onap.dmaap.interestingTopic";
		String c = "publisher";
		String[] a = { "sub", "view" };
		MR_Client sub = factory.genMR_Client("central",  f, c, a );
		String[] b = { "pub", "view" };
		MR_Client pub = factory.genMR_Client( "edge", f, c, b );
		ArrayList<MR_Client> clients = new ArrayList<MR_Client>();

		clients.add( sub );
		clients.add( pub );

		topic.setClients( clients );

		ts.reviewTopic( topic );
		ts.checkForBridge( topic, err );
		
		Topic nTopic = ts.addTopic( topic, err, false );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( t ));
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

		Topic topic = factory.genSimpleTopic("test5");
		Topic nTopic = ts.updateTopic( topic, err );

		assertTrue( err.getCode() == 200 );
	}
	
	@Test
	public void bridgeTest6() {
		ApiError err = new ApiError();

		String t = "org.onap.dmaap.bridgingTopic";
		Topic topic = factory.genSimpleTopic(t);
		topic.setReplicationCase( ReplicationType.REPLICATION_EDGE_TO_CENTRAL );

		String c = "publisher";
		String[] a = { "sub", "view" };
		MR_Client sub = factory.genMR_Client("central",  topic.getFqtn(), c, a );
		String[] b = { "pub", "view" };
		MR_Client pub = factory.genMR_Client( "edge", topic.getFqtn(), c, b );
		ArrayList<MR_Client> clients = new ArrayList<MR_Client>();

		clients.add( sub );
		clients.add( pub );

		topic.setClients( clients );

		Topic nTopic = ts.updateTopic( topic, err );

		assertTrue( err.getCode() == 200 );
	}
	@Test
	public void bridgeTest7() {
		ApiError err = new ApiError();

		String t = "org.onap.dmaap.bridgingTopic7";
		Topic topic = factory.genSimpleTopic(t);
		topic.setReplicationCase( ReplicationType.REPLICATION_CENTRAL_TO_EDGE );

		String c = "publisher";
		String[] a = { "sub", "view" };
		MR_Client sub = factory.genMR_Client("edge",  topic.getFqtn(), c, a );
		String[] b = { "pub", "view" };
		MR_Client pub = factory.genMR_Client( "central", topic.getFqtn(), c, b );
		ArrayList<MR_Client> clients = new ArrayList<MR_Client>();

		clients.add( sub );
		clients.add( pub );

		topic.setClients( clients );

		Topic nTopic = ts.updateTopic( topic, err );

		assertTrue( err.getCode() == 200 );
	}

}
