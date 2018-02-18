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

package org.onap.dmaap.dbcapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;







import org.onap.dmaap.dbcapi.aaf.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;

public class MR_ClusterService extends BaseLoggingClass {

	private Map<String, MR_Cluster> mr_clusters = DatabaseClass.getMr_clusters();
	
	public Map<String, MR_Cluster> getMR_Clusters() {			
		return mr_clusters;
	}
		
	public List<MR_Cluster> getAllMr_Clusters() {
		return new ArrayList<MR_Cluster>(mr_clusters.values());
	}
		
	public MR_Cluster getMr_Cluster( String key, ApiError apiError ) {			
		MR_Cluster mrc = mr_clusters.get( key );
		if ( mrc == null ) {
			apiError.setCode(Status.NOT_FOUND.getStatusCode());
			apiError.setFields( "dcaeLocationName");
			apiError.setMessage( "Cluster with dcaeLocationName " + key + " not found");
		}
		apiError.setCode(200);
		return mrc;
	}
	public MR_Cluster getMr_ClusterByFQDN( String key ) {		
		for( MR_Cluster cluster: mr_clusters.values() ) {
			if ( key.equals( cluster.getFqdn() ) ) {
				return cluster;
			}
		}
		return null;
	}
	
	public List<MR_Cluster> getCentralClusters() {
		DcaeLocationService locations = new DcaeLocationService();
		List<MR_Cluster> result = new ArrayList<MR_Cluster>();
		for( MR_Cluster c: mr_clusters.values() ) {
			if ( locations.getDcaeLocation(c.getDcaeLocationName()).isCentral() ) {
				result.add(c);
			}
		}
		return result;
	}	

	public MR_Cluster addMr_Cluster( MR_Cluster cluster, ApiError apiError ) {
		logger.info( "Entry: addMr_Cluster");
		MR_Cluster mrc = mr_clusters.get( cluster.getDcaeLocationName() );
		if ( mrc != null ) {
			apiError.setCode(Status.CONFLICT.getStatusCode());
			apiError.setFields( "dcaeLocationName");
			apiError.setMessage( "Cluster with dcaeLocationName " + cluster.getDcaeLocationName() + " already exists");
			return null;
		}
		cluster.setLastMod();
		cluster.setStatus(DmaapObject_Status.VALID);
		mr_clusters.put( cluster.getDcaeLocationName(), cluster );
		DcaeLocationService svc = new DcaeLocationService();
		DcaeLocation loc = svc.getDcaeLocation( cluster.getDcaeLocationName() );
		if ( loc != null && loc.isCentral() ) {
			ApiError resp = TopicService.setBridgeClientPerms( cluster );
			if ( ! resp.is2xx() ) {
				logger.error( "Unable to provision Bridge to " + cluster.getDcaeLocationName() );
				cluster.setLastMod();
				cluster.setStatus(DmaapObject_Status.INVALID);
				mr_clusters.put( cluster.getDcaeLocationName(), cluster );
			}
		}
		apiError.setCode(200);
		return cluster;
	}
		
	public MR_Cluster updateMr_Cluster( MR_Cluster cluster, ApiError apiError ) {
logger.info( "updateMr_Cluster:templogger: 10" );
		MR_Cluster mrc = mr_clusters.get( cluster.getDcaeLocationName() );
logger.info( "updateMr_Cluster:templogger: 20" );
		if ( mrc == null ) {
			apiError.setCode(Status.NOT_FOUND.getStatusCode());
			apiError.setFields( "dcaeLocationName");
			apiError.setMessage( "Cluster with dcaeLocationName " + cluster.getDcaeLocationName() + " not found");
			return null;
		}
logger.info( "updateMr_Cluster:templogger: 30" );
		cluster.setLastMod();
logger.info( "updateMr_Cluster:templogger: 40" );
		mr_clusters.put( cluster.getDcaeLocationName(), cluster );
logger.info( "updateMr_Cluster:templogger: 50" );
		DcaeLocationService svc = new DcaeLocationService();
logger.info( "updateMr_Cluster:templogger: 60" );
		DcaeLocation loc = svc.getDcaeLocation( cluster.getDcaeLocationName() );
		if ( loc == null ) {
			logger.error( "DcaeLocation not found for cluster in " + cluster.getDcaeLocationName() );
			cluster.setLastMod();
			cluster.setStatus(DmaapObject_Status.INVALID);
			mr_clusters.put( cluster.getDcaeLocationName(), cluster );
logger.info( "updateMr_Cluster:templogger: 70" );
		} else if ( loc.isCentral() ) {
logger.info( "updateMr_Cluster:templogger: 80" );
			ApiError resp = TopicService.setBridgeClientPerms( cluster );
logger.info( "updateMr_Cluster:templogger: 90" );
			if ( ! resp.is2xx() ) {
logger.info( "updateMr_Cluster:templogger: 95" );
				logger.error( "Unable to provision Bridge to " + cluster.getDcaeLocationName() );
				cluster.setLastMod();
				cluster.setStatus(DmaapObject_Status.INVALID);
				mr_clusters.put( cluster.getDcaeLocationName(), cluster );
			}
		}
logger.info( "updateMr_Cluster:templogger: 100" );
		apiError.setCode(200);
		return cluster;
	}
		
	public MR_Cluster removeMr_Cluster( String key, ApiError apiError ) {
		MR_Cluster mrc = mr_clusters.get( key );
		if ( mrc == null ) {
			apiError.setCode(Status.NOT_FOUND.getStatusCode());
			apiError.setFields( "dcaeLocationName");
			apiError.setMessage( "Cluster with dcaeLocationName " + key + " not found");
			return null;
		}
		apiError.setCode(200);
		return mr_clusters.remove(key);
	}	
	

}
