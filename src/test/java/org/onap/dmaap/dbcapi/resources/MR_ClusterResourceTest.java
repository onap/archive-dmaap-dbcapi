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
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;


public class MR_ClusterResourceTest extends JerseyTest {

	static DmaapObjectFactory factory = new DmaapObjectFactory();

	@Override
	protected Application configure() {

		return new ResourceConfig()
				.register( MR_ClusterResource.class )
				.register( DcaeLocationResource.class );
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
		Response resp = target( "mr_clusters").request().get( Response.class );
		System.out.println( "GET feed resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}
	@Test
	public void PostTest() {
		MR_Cluster cluster = factory.genMR_Cluster( "central" );
		Entity<MR_Cluster> reqEntity = Entity.entity( cluster, MediaType.APPLICATION_JSON );
		Response resp = target( "mr_clusters").request().post( reqEntity, Response.class );
		System.out.println( "POST MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		if (resp.getStatus() != 409 ) {
			assertTrue( resp.getStatus() == 201);
		}
		resp = target( "mr_clusters").
				path( cluster.getDcaeLocationName()).request().get( Response.class );
		System.out.println( "GET MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
	
		assertTrue( resp.getStatus() >= 200 && resp.getStatus() < 300 );
		
	}

	@Test
	public void PutTest() {

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
		
		String h[] = {"host4", "host5", "host6" };
		MR_Cluster cluster = factory.genMR_Cluster( "central" );
		Entity<MR_Cluster> reqEntity = Entity.entity( cluster, MediaType.APPLICATION_JSON );
		Response resp = target( "mr_clusters").request().post( reqEntity, Response.class );

		// first, add it 
		System.out.println( "POST MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		if( resp.getStatus() != 409 ) {
			assertTrue( resp.getStatus() == 200 );
		}

		// now change a field
		cluster.setHosts( h );
		reqEntity = Entity.entity( cluster, MediaType.APPLICATION_JSON );

		// update with incorrect key
		resp = target( "mr_clusters")
					.path( cluster.getDcaeLocationName())
					.request()
					.put( reqEntity, Response.class );
		System.out.println( "PUT MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity(String.class));
		assertTrue( resp.getStatus() == 200 );

		// update with correct key
		resp = target( "mr_clusters")
					.path( cluster.getDcaeLocationName())
					.request()
					.put( reqEntity, Response.class );
		System.out.println( "PUT MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity(String.class));
		assertTrue( resp.getStatus() == 200 );
	}

	@Test
	public void DelTest() {

		try {
			DcaeLocation loc = factory.genDcaeLocation( "edge" );
			Entity<DcaeLocation> reqEntity = Entity.entity( loc, MediaType.APPLICATION_JSON );
			Response resp = target( "dcaeLocations").request().post( reqEntity, Response.class );
			System.out.println( "POST dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ));
			if ( resp.getStatus() != 409 ) {
				assertTrue( resp.getStatus() == 201 );
			}
		} catch (Exception e ) {
		}
		

		MR_Cluster cluster = factory.genMR_Cluster( "edge" );

		Response resp = target( "mr_clusters").
				path( cluster.getDcaeLocationName()).
				request().
				delete( Response.class );

		// confirm cluster is not there 
		System.out.println( "DELETE MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 404 );
		
		// now, add it
		Entity<MR_Cluster> reqEntity = Entity.entity( cluster, MediaType.APPLICATION_JSON );
		 resp = target( "mr_clusters").request().post( reqEntity, Response.class );

		
		System.out.println( "POST MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 201 );
	
		// now really delete it 
		 resp = target( "mr_clusters").
				path( cluster.getDcaeLocationName()).
				request().
				delete( Response.class );
		System.out.println( "DELETE MR_Cluster resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 204 );

	}



}

