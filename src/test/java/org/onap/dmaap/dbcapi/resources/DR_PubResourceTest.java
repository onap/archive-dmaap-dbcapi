
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
package org.onap.dmaap.dbcapi.resources;
import org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.service.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.sql.*;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Path;
import javax.ws.rs.GET;

public class DR_PubResourceTest extends JerseyTest{

	@Override
	protected Application configure() {
		return new ResourceConfig()
				.register( DR_PubResource.class )
				.register( FeedResource.class );
	}

	private static final String  fmt = "%24s: %s%n";
	String d, un, up, f, p;
/*
	@Before
	public void setUp() throws Exception {
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";
		f = "234";
		p = "678";
	}

	@After
	public void tearDown() throws Exception {
	}
*/



/*  may conflict with test framework! 
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
*/

	private Feed addFeed( String name, String desc ) {
		Feed feed = new Feed( name, "1.0", desc, "dgl", "unrestricted" );
		Entity<Feed> reqEntity = Entity.entity( feed, MediaType.APPLICATION_JSON );
		Response resp = target( "feeds").request().post( reqEntity, Response.class );
		int rc = resp.getStatus();
		System.out.println( "POST feed resp=" + rc );
		assertTrue( rc == 200 || rc == 409 );
		feed = resp.readEntity( Feed.class );
		return feed;
	}
	
	private DR_Pub addPub( String d, String un, String up, String feedId ) {
		DR_Pub dr_pub = new DR_Pub( d, un, up, feedId, "" );
		Entity<DR_Pub> reqEntity2 = Entity.entity( dr_pub, MediaType.APPLICATION_JSON);
		Response resp = target( "dr_pubs").request().post( reqEntity2, Response.class);
		System.out.println( "POST dr_pubs resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 201 );
		dr_pub = resp.readEntity( DR_Pub.class );
		
		return dr_pub;
	}
	
	private DR_Pub addPubByName( String d, String un, String up, String feedName) {
		DR_Pub dr_pub = new DR_Pub( d, un, up, null, "" );
		dr_pub.setFeedName(feedName);
		Entity<DR_Pub> reqEntity2 = Entity.entity( dr_pub, MediaType.APPLICATION_JSON);
		Response resp = target( "dr_pubs").request().post( reqEntity2, Response.class);
		System.out.println( "POST dr_pubs resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 201 );
		dr_pub = resp.readEntity( DR_Pub.class );
		
		return dr_pub;
	}
	
	@Test
	public void GetTest() {
		Response resp = target( "dr_pubs").request().get( Response.class );
		System.out.println( "GET dr_pubs resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}
	
	@Test
	public void PostTest() {

		Feed feed = addFeed( "pubPostTest", "post unit test" );
		System.out.println( "fpubPostTest: feedId=" + feed.getFeedId());
		
		String d, un, up;
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";

		DR_Pub dr_pub = addPub( d, un, up, feed.getFeedId() );
	}
	
	@Test
	public void PostTestByName() {

		Feed feed = addFeed( "pubPostTest2", "post unit test" );
		System.out.println( "fpubPostTest: feedId=" + feed.getFeedId());
		
		String d, un, up;
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";

		DR_Pub dr_pub = addPubByName( d, un, up, "pubPostTest2" );	
	}

	@Test
	public void PutTest() {

		Feed feed = addFeed( "pubPutTest", "put unit test");
		String d, un, up;
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";

		DR_Pub dr_pub = addPub( d, un, up, feed.getFeedId() );
		
		dr_pub.setUserpwd("newSecret");
		Entity<DR_Pub> reqEntity2 = Entity.entity( dr_pub, MediaType.APPLICATION_JSON);
		Response resp = target( "dr_pubs")
				.path( dr_pub.getPubId() )
				.request()
				.put( reqEntity2, Response.class);
		System.out.println( "PUT dr_pubs resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 200 );
	}
		


}


