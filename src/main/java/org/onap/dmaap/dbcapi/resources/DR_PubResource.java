/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Pub;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.service.ApiService;
import org.onap.dmaap.dbcapi.service.DR_PubService;
import org.onap.dmaap.dbcapi.service.FeedService;


@Path("/dr_pubs")
@Api( value= "dr_pubs", description = "Endpoint for a Data Router client that implements a Publisher" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class DR_PubResource extends BaseLoggingClass {

	private DR_PubService dr_pubService = new DR_PubService();
	private ResponseBuilder responseBuilder = new ResponseBuilder();
	
	@GET
	@ApiOperation( value = "return DR_Pub details", 
	notes = "Returns array of  `DR_Pub` objects.  Add filter for feedId.", 
	response = DR_Pub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Pub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public  Response getDr_Pubs() {
		logger.info( "Entry: GET /dr_pubs");
		List<DR_Pub> pubs = dr_pubService.getAllDr_Pubs();

		GenericEntity<List<DR_Pub>> list = new GenericEntity<List<DR_Pub>>(pubs) {
        };
        return responseBuilder.success(list);
	}
	
	@POST
	@ApiOperation( value = "return DR_Pub details", 
	notes = "create a DR Publisher in the specified environment.", 
	response = DR_Pub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Pub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response addDr_Pub( 
			DR_Pub pub
			) {
		ApiService resp = new ApiService();
		FeedService feeds = new FeedService();
		Feed fnew = null;

		logger.info( "Entry: POST /dr_pubs");

		try {
			resp.required( "feedId", pub.getFeedId(), "");
		} catch ( RequiredFieldException rfe ) {
			try {
				resp.required( "feedName", pub.getFeedName(), "");
			}catch ( RequiredFieldException rfe2 ) {
				logger.debug( resp.toString() );
				return responseBuilder.error(resp.getErr());
			}
			// if we found a FeedName instead of a FeedId then try to look it up.
			List<Feed> nfeeds =  feeds.getAllFeeds( pub.getFeedName(), pub.getFeedVersion(), "equals");
			if ( nfeeds.size() != 1 ) {
				logger.debug( "Attempt to match "+ pub.getFeedName() + " ver="+pub.getFeedVersion() + " matched " + nfeeds.size() );
				return responseBuilder.error(resp.getErr());
			}
			fnew = nfeeds.get(0);
		}
		try {
			resp.required( "dcaeLocationName", pub.getDcaeLocationName(), "");
		} catch ( RequiredFieldException rfe ) {
			logger.debug( resp.getErr().toString() );
			return responseBuilder.error(resp.getErr());
		}


		// we may have fnew already if located by FeedName
		if ( fnew == null ) {
			fnew = feeds.getFeed( pub.getFeedId(), resp.getErr() );
		}
		if ( fnew == null ) {
			logger.info( "Specified feed " + pub.getFeedId() + " or " + pub.getFeedName() + " not known to Bus Controller");	
			return responseBuilder.error(resp.getErr());
		}

		ArrayList<DR_Pub> pubs = fnew.getPubs();
		logger.info( "num existing pubs before = " + pubs.size() );
		
		logger.info( "update feed");
		pub.setNextPubId();
		if ( pub.getUsername() == null ) {
			pub.setRandomUserName();
		}
		if ( pub.getUserpwd() == null ) {
			pub.setRandomPassword();
		}
		pubs.add( pub );
		fnew.setPubs(pubs);
		fnew = feeds.updateFeed( fnew, resp.getErr() );	
		
		if ( ! resp.getErr().is2xx()) {	
			return responseBuilder.error(resp.getErr());
		}
		pubs = fnew.getPubs();
		logger.info( "num existing pubs after = " + pubs.size() );
		
		DR_Pub pnew = dr_pubService.getDr_Pub(pub.getPubId(), resp.getErr());
		return responseBuilder.success(Status.CREATED.getStatusCode(), pnew);
	}
	
	@PUT
	@ApiOperation( value = "return DR_Pub details", 
	notes = "update a DR Publisher in the specified environment.  Update a `DR_Pub` object by pubId", 
	response = DR_Pub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Pub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{pubId}")
	public Response updateDr_Pub( 
			@PathParam("pubId") String name, 
			DR_Pub pub
			) {
		logger.info( "Entry: PUT /dr_pubs");
		pub.setPubId(name);
		DR_Pub res = dr_pubService.updateDr_Pub(pub);
		return responseBuilder.success(res);
	}
	
	@DELETE
	@ApiOperation( value = "return DR_Pub details", 
	notes = "delete a DR Publisher in the specified environment. Delete a `DR_Pub` object by pubId", 
	response = DR_Pub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 204, message = "Success", response = DR_Pub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{pubId}")
	public Response deleteDr_Pub( 
			@PathParam("pubId") String id
			){

		ApiService resp = new ApiService();

		try {
			resp.required( "pubId", id, "");
		} catch ( RequiredFieldException rfe ) {
			return responseBuilder.error(resp.getErr());
		}

		DR_Pub pub =  dr_pubService.getDr_Pub( id, resp.getErr() );
		if ( ! resp.getErr().is2xx()) {	
			return responseBuilder.error(resp.getErr());
		}
		FeedService feeds = new FeedService();
		Feed fnew = feeds.getFeed( pub.getFeedId(), resp.getErr() );
		if ( fnew == null ) {
			logger.info( "Specified feed " + pub.getFeedId() + " not known to Bus Controller");	
			return responseBuilder.error(resp.getErr());
		}
		ArrayList<DR_Pub> pubs = fnew.getPubs();
		if ( pubs.size() == 1 ) {
			resp.setCode(Status.BAD_REQUEST.getStatusCode());
			resp.setMessage( "Can't delete the last publisher of a feed");
			return responseBuilder.error(resp.getErr());
		}
		
		for( Iterator<DR_Pub> i = pubs.iterator(); i.hasNext(); ) {
			DR_Pub listItem = i.next();
			if ( listItem.getPubId().equals(id)) {
				i.remove();
			}
		}
		fnew.setPubs(pubs);
		fnew = feeds.updateFeed( fnew, resp.getErr() );
		if ( ! resp.getErr().is2xx()) {	
			return responseBuilder.error(resp.getErr());
		}
		
		dr_pubService.removeDr_Pub(id, resp.getErr() );
		if ( ! resp.getErr().is2xx()) {	
			return responseBuilder.error(resp.getErr());
		}
		return responseBuilder.success(Status.NO_CONTENT.getStatusCode(), null);
	}

	@GET
	@ApiOperation( value = "return DR_Pub details", 
	notes = "returns a DR Publisher in the specified environment. Gets a `DR_Pub` object by pubId", 
	response = DR_Pub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Pub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{pubId}")
	public Response get( 
			@PathParam("pubId") String id
			) {
		ApiService resp = new ApiService();

		try {
			resp.required( "feedId", id, "");
		} catch ( RequiredFieldException rfe ) {
			return responseBuilder.error(resp.getErr());
		}

		DR_Pub pub =  dr_pubService.getDr_Pub( id, resp.getErr() );
		if ( ! resp.getErr().is2xx()) {	
			resp.getErr();			
		}
		return responseBuilder.success(Status.OK.getStatusCode(), pub);
	}
}
