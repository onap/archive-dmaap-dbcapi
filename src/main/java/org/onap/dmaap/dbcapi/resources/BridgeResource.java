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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.onap.dmaap.dbcapi.aaf.authentication.AuthenticationErrorException;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.BrTopic;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.model.MirrorMaker;
import org.onap.dmaap.dbcapi.service.ApiService;
import org.onap.dmaap.dbcapi.service.MirrorMakerService;

@Path("/bridge")
@Api( value= "bridge", description = "Endpoint for retreiving MR Bridge metrics" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class BridgeResource extends BaseLoggingClass {
	
	private MirrorMakerService mmService = new MirrorMakerService();

	@GET
	@ApiOperation( value = "return BrTopic details", 
	notes = "Returns array of  `BrTopic` objects. If source and target query params are specified, only report on that bridge.  If detail param is true, list topics names, else just a count is returned", 
	response = BrTopic.class)
@ApiResponses( value = {
    @ApiResponse( code = 200, message = "Success", response = Dmaap.class),
    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
})
	public Response	getBridgedTopics(@QueryParam("source") String source,
						   			@QueryParam("target") String target,
						   			@QueryParam("detail") Boolean detailFlag ){
		ApiService check = new ApiService();

		if ( ! Boolean.TRUE.equals(detailFlag)) {
			BrTopic brTopic = new BrTopic();
			
			logger.info( "getBridgeTopics():" + " source=" + source + ", target=" + target);
	//		System.out.println("getBridgedTopics() " + "source=" + source + ", target=" + target );
			if (source != null && target != null) {		// get topics between 2 bridged locations
				brTopic.setBrSource(source);
				brTopic.setBrTarget(target);
				MirrorMaker mm = mmService.getMirrorMaker(source, target);
				if ( mm != null ) {		
						brTopic.setTopicCount( mm.getTopicCount() );
				} 
	
				logger.info( "topicCount [2 locations]: " + brTopic.getTopicCount() );
			}
			else if (source == null && target == null ) {
				List<String> mmList = mmService.getAllMirrorMakers();
				brTopic.setBrSource("all");
				brTopic.setBrTarget("all");
				int totCnt = 0;
				for( String key: mmList ) {
					int mCnt = 0;
					MirrorMaker mm = mmService.getMirrorMaker(key);
					if ( mm != null ) {
						mCnt = mm.getTopicCount();
					}
					logger.info( "Count for "+ key + ": " + mCnt);
					totCnt += mCnt;
				}
				
				logger.info( "topicCount [all locations]: " + totCnt );
				brTopic.setTopicCount(totCnt);
	
			}
			else {
	
				logger.error( "source or target is missing");
				check.setCode(Status.BAD_REQUEST.getStatusCode());
				check.setMessage("Either both source and target or neither must be provided");
				return check.error();
			}
			return check.success(brTopic);
		} else {
			
			
			logger.info( "getBridgeTopics() detail:" + " source=" + source + ", target=" + target);
	
			if (source != null && target != null) {		// get topics between 2 bridged locations
				
				MirrorMaker mm = mmService.getMirrorMaker(source, target);
				if ( mm == null ) {		
					return check.notFound();
				} 
	
				return check.success(mm);
			}

			else {
	
				logger.error( "source and target are required when detail=true");
				check.setCode(Status.BAD_REQUEST.getStatusCode());
				check.setMessage("source and target are required when detail=true");
				return check.error();
			}
		}
	}
	
	@PUT
	@ApiOperation( value = "update MirrorMaker details", 
		notes = "replace the topic list for a specific Bridge.  Use JSON Body for value to replace whitelist, but if refreshFlag param is true, simply refresh using existing whitelist", 
		response = MirrorMaker.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = Dmaap.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response	putBridgedTopics(@QueryParam("source") String source,
						   			@QueryParam("target") String target,
						   			@QueryParam("refresh") Boolean refreshFlag,
						   			MirrorMaker newBridge ){
		ApiService check = new ApiService();	
			
		logger.info( "putBridgeTopics() detail:" + " source=" + source + ", target=" + target);

		if (source != null && target != null) {		// get topics between 2 bridged locations
			
			MirrorMaker mm = mmService.getMirrorMaker(source, target);
			if ( mm == null ) {		
				return check.notFound();
			} 
			if ( refreshFlag != null  &&  refreshFlag == false ) {
				logger.info( "setting whitelist from message body");
				mm.setTopics( newBridge.getTopics() );
			} else {
				logger.info( "refreshing whitelist from memory");
			}
			mmService.updateMirrorMaker(mm);
			return check.success(mm);
		}

		else {

			logger.error( "source and target are required when detail=true");
			check.setCode(Status.BAD_REQUEST.getStatusCode());
			check.setMessage("source and target are required when detail=true");
			return check.error();
		}

	}
}
