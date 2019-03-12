
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
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

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

public class DR_SubResourceTest extends JerseyTest{
	
	static DmaapObjectFactory factory = new DmaapObjectFactory();

	@Override
	protected Application configure() {
		return new ResourceConfig()
				.register( DR_SubResource.class )
				.register( FeedResource.class )
				.register( DcaeLocationResource.class )
				.register( DmaapResource.class );
	}

	String d, un, up, f, p;
	
	@Before
	public void preTest() throws Exception {
		try {

			Dmaap dmaap = factory.genDmaap();
			Entity<Dmaap> reqEntity = Entity.entity( dmaap, MediaType.APPLICATION_JSON );
			Response resp = target( "dmaap").request().post( reqEntity, Response.class );
			System.out.println( resp.getStatus() );
			assertTrue( resp.getStatus() == 200 );
		}catch (Exception e ) {
		}
		try {
			DcaeLocation loc = factory.genDcaeLocation( "central" );
			Entity<DcaeLocation> reqEntity = Entity.entity( loc, MediaType.APPLICATION_JSON );
			Response resp = target( "dcaeLocations").request().post( reqEntity, Response.class );
			System.out.println( "POST dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ));
			if ( resp.getStatus() != 409 ) {
				assertTrue( resp.getStatus() == 201 );
			}
		} catch (Exception e ) {
		}
	

	}
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
	
	private DR_Sub addSub( String d, String un, String up, String feedId ) {
		DR_Sub dr_sub = new DR_Sub( d, un, up, feedId, 
				"https://subscriber.onap.org/foo", "https://dr-prov/sublog", true );

		Entity<DR_Sub> reqEntity2 = Entity.entity( dr_sub, MediaType.APPLICATION_JSON);
		Response resp = target( "dr_subs").request().post( reqEntity2, Response.class);
		System.out.println( "POST dr_subs resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 201 );
		dr_sub = resp.readEntity( DR_Sub.class );
		
		return dr_sub;
	}
	private DR_Sub addSubByName( String d, String un, String up, String feedName ) {
		DR_Sub dr_sub = new DR_Sub( d, un, up, null, 
				"https://subscriber.onap.org/foo", "https://dr-prov/sublog", true );
		
		dr_sub.setFeedName(feedName);

		Entity<DR_Sub> reqEntity2 = Entity.entity( dr_sub, MediaType.APPLICATION_JSON);
		Response resp = target( "dr_subs").request().post( reqEntity2, Response.class);
		System.out.println( "POST dr_subs resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 201 );
		dr_sub = resp.readEntity( DR_Sub.class );
		
		return dr_sub;
	}

	@Test
	public void GetTest() {
		Response resp = target( "dr_subs").request().get( Response.class );
		System.out.println( "GET dr_subs resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}
	
	@Test
	public void PostTest() {

		Feed feed = addFeed( "subPostTest", "post unit test" );
		System.out.println( "subPostTest: feedId=" + feed.getFeedId());
		
		String d, un, up;
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";

		DR_Sub dr_pub = addSub( d, un, up, feed.getFeedId() );
	}

	@Test
	public void PostTestByName() {

		Feed feed = addFeed( "subPostTest2", "post unit test" );
		System.out.println( "subPostTest2: feedId=" + feed.getFeedId());
		
		String d, un, up;
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";

		DR_Sub dr_pub = addSubByName( d, un, up, feed.getFeedName() );
	}

	@Test
	public void PutTest() {

		Feed feed = addFeed( "subPutTest", "put unit test");
		String d, un, up;
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";

		DR_Sub dr_sub = addSub( d, un, up, feed.getFeedId() );
		
		dr_sub.setUserpwd("newSecret");
		Entity<DR_Sub> reqEntity2 = Entity.entity( dr_sub, MediaType.APPLICATION_JSON);
		Response resp = target( "dr_subs")
				.path( dr_sub.getSubId() )
				.request()
				.put( reqEntity2, Response.class);
		System.out.println( "PUT dr_subs resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 200 );
	}


// TODO: figure out how to check delete() response
	@Test
	public void DelTest() {

		Feed feed = addFeed( "subDelTest", "del unit test");
		String d, un, up;
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";

		DR_Sub dr_sub = addSub( d, un, up, feed.getFeedId() );
		
		Entity<DR_Sub> reqEntity2 = Entity.entity( dr_sub, MediaType.APPLICATION_JSON);
		Response resp = target( "dr_subs")
				.path( dr_sub.getSubId() )
				.request()
				.delete();
		System.out.println( "DEL dr_subs resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 204 );
	}


}


