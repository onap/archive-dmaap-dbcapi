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

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;


public class TopicResourceTest extends JerseyTest {

	static DmaapObjectFactory factory = new DmaapObjectFactory();

	@Override
	protected Application configure() {

		return new ResourceConfig()
				.register( TopicResource.class )
				.register( MR_ClusterResource.class )
				.register( DcaeLocationResource.class )
				.register( DmaapResource.class );
	}

	private static final String  fmt = "%24s: %s%n";


	@Before
	public void preTest() throws Exception {
		DatabaseClass.clearDatabase();
		try {

			Dmaap dmaap = factory.genDmaap();
			Entity<Dmaap> reqEntity = Entity.entity( dmaap, MediaType.APPLICATION_JSON );
			Response resp = target( "dmaap").request().put( reqEntity, Response.class );
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
		try {
			MR_Cluster cluster = factory.genMR_Cluster( "central" );
			Entity<MR_Cluster> reqEntity = Entity.entity( cluster, MediaType.APPLICATION_JSON );
			Response resp = target( "mr_clusters").request().post( reqEntity, Response.class );
			System.out.println( "POST MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
			if (resp.getStatus() != 409 ) {
				assertTrue( resp.getStatus() == 200 );
			}	
		} catch (Exception e ) {
			
		}

	}
	/*  may conflict with test framework! 
	@After
	public void tearDown() throws Exception {
	}
*/


	@Test
	public void GetTest() {
		Response resp = target( "topics").request().get( Response.class );
		System.out.println( "GET feed resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}
	

	@Test
	public void PostTest() {
		Topic topic = factory.genSimpleTopic( "test1" );
		Entity<Topic> reqEntity = Entity.entity( topic, MediaType.APPLICATION_JSON );
		Response resp = target( "topics").request().post( reqEntity, Response.class );
		System.out.println( "POST Topic resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		if (resp.getStatus() != 409 ) {
			assertTrue( resp.getStatus() == 201);
		}
		resp = target( "topics").
				path( topic.genFqtn() ).request().get( Response.class );
		System.out.println( "GET Topic resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
	
		assertTrue( resp.getStatus() == 200 );
		
	}


	@Test
	public void PutTest() {

		Topic topic = factory.genSimpleTopic( "test2" );
		Entity<Topic> reqEntity = Entity.entity( topic, MediaType.APPLICATION_JSON );
		Response resp = target( "topics").request().post( reqEntity, Response.class );
		String json = resp.readEntity(String.class);
		System.out.println( "POST Topic resp=" + resp.getStatus() + " " + json );
		if (resp.getStatus() != 409 ) {
			assertTrue( resp.getStatus() == 201);
		}

		
		// now change a field
		topic.setOwner( "newbody" );
		reqEntity = Entity.entity( topic, MediaType.APPLICATION_JSON );

		// update with incorrect key
		resp = target( "topics")
					.path( "org.onap.dmaap.notATopic" )
					.request()
					.put( reqEntity, Response.class );
		
		System.out.println( "PUT Topic resp=" + resp.getStatus() + " expect 400" );
		assertTrue( resp.getStatus() == 400 );

		// update with correct key
		topic = new Topic( json );
		reqEntity = Entity.entity( topic, MediaType.APPLICATION_JSON );
		resp = target( "topics")
					.path( topic.getFqtn())
					.request()
					.put( reqEntity, Response.class );
		System.out.println( "PUT Topic resp=" + resp.getStatus() + " " + resp.readEntity(String.class));
		assertTrue( resp.getStatus() == 400 );  // PUT is not allowed even with the right key
	}

	@Test
	public void DelTest() {

		Topic topic = factory.genSimpleTopic( "test3" );
		topic.setFqtn( "org.onap.unittest.test3" );
		
		Response resp = target( "topics").
				path( topic.getFqtn() ).
				request().
				delete( Response.class );

		// confirm topic is not there 
		System.out.println( "DELETE Topic resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 404 );
		
		// now, add it
		Entity<Topic> reqEntity = Entity.entity( topic, MediaType.APPLICATION_JSON );
		resp = target( "topics").request().post( reqEntity, Response.class );
		String json = resp.readEntity( String.class );
		System.out.println( "POST Topic resp=" + resp.getStatus() + " " + json );
		assertTrue( resp.getStatus() == 201 );
		
		topic = new Topic( json );
		// now really delete it 
		 resp = target( "topics").
				path( topic.getFqtn()).
				request().
				delete( Response.class );
		System.out.println( "DELETE Topic resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 204 );

	}



}

