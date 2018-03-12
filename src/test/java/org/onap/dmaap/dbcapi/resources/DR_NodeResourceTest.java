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


public class DR_NodeResourceTest extends JerseyTest {

	static DmaapObjectFactory factory = new DmaapObjectFactory();
	static String entry_path = "dr_nodes";

	@Override
	protected Application configure() {
		return new ResourceConfig( DR_NodeResource.class );
	}

	private static final String  fmt = "%24s: %s%n";



/*  may conflict with test framework! 
	@Before
	public void preTest() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
*/


	@Test
	public void GetTest() {
		Response resp = target( entry_path ).request().get( Response.class );
		System.out.println( "GET " + entry_path + " resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}
	@Test
	public void PostTest() {
		DR_Node node = factory.genDR_Node( "central" );
		Entity<DR_Node> reqEntity = Entity.entity( node, MediaType.APPLICATION_JSON );
		Response resp = target( entry_path ).request().post( reqEntity, Response.class );
		System.out.println( "POST " + entry_path + " resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 200 );
	}

	@Test
	public void PutTest() {

/*
		try {
			DcaeLocation loc = factory.genDcaeLocation( "central" );
			Entity<DcaeLocation> reqEntity = Entity.entity( loc, MediaType.APPLICATION_JSON );
			Response resp = target( "dcaeLocations").request().post( reqEntity, Response.class );
			System.out.println( "POST dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ));
			assertTrue( resp.getStatus() == 201 );
		} catch (Exception e ) {
		}
*/

		DR_Node node = factory.genDR_Node( "central" );
		Entity<DR_Node> reqEntity = Entity.entity( node, MediaType.APPLICATION_JSON );
		Response resp = target( entry_path ).request().post( reqEntity, Response.class );

		// first, add it 
		System.out.println( "POST " + entry_path + " resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 200 );

		// now change a field
		node.setVersion( "1.0.2" );
		reqEntity = Entity.entity( node, MediaType.APPLICATION_JSON );

		// update  currently fails...
		resp = target( entry_path )
					.path( node.getFqdn())
					.request()
					.put( reqEntity, Response.class );
		System.out.println( "PUT " + entry_path + "/" + node.getFqdn() + " resp=" + resp.getStatus() + " " + resp.readEntity(String.class));
		assertTrue( resp.getStatus() == 404 );

	}




}

