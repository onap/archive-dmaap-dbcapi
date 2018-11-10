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









//import org.openecomp.dmaapbc.aaf.AndrewDecryptor;
import org.onap.dmaap.dbcapi.aaf.AafDecrypt;
import org.onap.dmaap.dbcapi.client.MrTopicConnection;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.MirrorMaker;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.RandomInteger;

public class MirrorMakerService extends BaseLoggingClass {
	
	private Map<String, MirrorMaker> mirrors = DatabaseClass.getMirrorMakers();
	private static MrTopicConnection prov;
	private static AafDecrypt decryptor;
	
	private static String provUser;
	private static String provUserPwd;
	private static String defaultProducerPort;
	private static String defaultConsumerPort;
	private static String centralFqdn;
	private int maxTopicsPerMM;
	
	public MirrorMakerService() {
		super();
		decryptor = new AafDecrypt();
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		provUser = p.getProperty("MM.ProvUserMechId");
		provUserPwd = decryptor.decrypt(p.getProperty( "MM.ProvUserPwd", "notSet" ));
		defaultProducerPort = p.getProperty( "MR.SourceReplicationPort", "9092");
		defaultConsumerPort = p.getProperty( "MR.TargetReplicationPort", "2181");	
		centralFqdn = p.getProperty("MR.CentralCname", "notSet");
		maxTopicsPerMM = Integer.valueOf( p.getProperty( "MaxTopicsPerMM", "5"));
	}

	// will create a MM on MMagent if needed
	// will update the MMagent whitelist with all topics for this MM
	public MirrorMaker updateMirrorMaker( MirrorMaker mm ) {
		logger.info( "updateMirrorMaker");
	
		prov = new MrTopicConnection( provUser, provUserPwd );
	
		DmaapService dmaap = new DmaapService();
		MR_ClusterService clusters = new MR_ClusterService();
	
		// in 1610, MM should only exist for edge-to-central
		//  we use a cname for the central MR cluster that is active, and provision on agent topic on that target
		// but only send 1 message so MM Agents can read it relying on kafka delivery
		for( MR_Cluster central: clusters.getCentralClusters() ) {
			prov.makeTopicConnection(central, dmaap.getBridgeAdminFqtn(), centralFqdn  );
			ApiError resp = prov.doPostMessage(mm.createMirrorMaker( defaultConsumerPort, defaultProducerPort ));
			if ( ! resp.is2xx() ) {
	
				errorLogger.error( DmaapbcLogMessageEnum.MM_PUBLISH_ERROR, "create MM", Integer.toString(resp.getCode()), resp.getMessage());
				mm.setStatus(DmaapObject_Status.INVALID);
			} else {
				prov.makeTopicConnection(central, dmaap.getBridgeAdminFqtn(), centralFqdn );
				resp = prov.doPostMessage(mm.getWhitelistUpdateJSON());
				if ( ! resp.is2xx()) {
					errorLogger.error( DmaapbcLogMessageEnum.MM_PUBLISH_ERROR,"MR Bridge", Integer.toString(resp.getCode()), resp.getMessage());
					mm.setStatus(DmaapObject_Status.INVALID);
				} else {
					mm.setStatus(DmaapObject_Status.VALID);
				}
			}
			
			// we only want to send one message even if there are multiple central clusters
			break;
		
		} 
		


		mm.setLastMod();
		return mirrors.put( mm.getMmName(), mm);
	}
	public MirrorMaker getMirrorMaker( String part1, String part2, int index ) {
		String targetPart;

		// original mm names did not have any index, so leave off index 0 for
		// backwards compatibility
		if ( index == 0 ) {
			targetPart = part2;
		} else {
			targetPart = part2 + "_" + index;
		}
		logger.info( "getMirrorMaker using " + part1 + " and " + targetPart );
		return mirrors.get(MirrorMaker.genKey(part1, targetPart));
	}
	public MirrorMaker getMirrorMaker( String part1, String part2 ) {
		logger.info( "getMirrorMaker using " + part1 + " and " + part2 );
		return mirrors.get(MirrorMaker.genKey(part1, part2));
	}	
	public MirrorMaker getMirrorMaker( String key ) {
		logger.info( "getMirrorMaker using " + key);
		return mirrors.get(key);
	}
	
	
	public void delMirrorMaker( MirrorMaker mm ) {
		logger.info( "delMirrorMaker");
		mirrors.remove(mm.getMmName());
	}
	
	// TODO: this should probably return sequential values or get replaced by the MM client API
	// but it should be sufficient for initial 1610 development
	public static String genTransactionId() {
		RandomInteger ri = new RandomInteger(100000);
	    int randomInt = ri.next();
	    return Integer.toString(randomInt);
	}
	public List<String> getAllMirrorMakers() {
		List<String> ret = new ArrayList<String>();
		for( String key: mirrors.keySet()) {
			ret.add( key );
		}
		
		return ret;
	}
	
	public MirrorMaker getNextMM( String source, String target ) {
		int i = 0;
		MirrorMaker mm = null;
		while( mm == null ) {
			
			mm = this.getMirrorMaker( source, target, i);
			if ( mm == null ) {
				mm = new MirrorMaker(source, target, i);
			}
			if ( mm.getTopicCount() >= maxTopicsPerMM ) {
				logger.info( "getNextMM: MM " + mm.getMmName() + " has " + mm.getTopicCount() + " topics.  Moving to next MM");
				i++;
				mm = null;
			}
		}
	 
		
		return mm;
	}

	public MirrorMaker splitMM( MirrorMaker orig ) {
		
		int index = 1;
		String source = orig.getSourceCluster();
		String target = orig.getTargetCluster();
		
		
		ArrayList<String> whitelist = orig.getTopics();
		while( whitelist.size() > maxTopicsPerMM ) {
			MirrorMaker mm = this.getNextMM( source, target );
			int last = whitelist.size() - 1;
			String topic = whitelist.get(last);
			whitelist.remove(last);
			mm.addTopic(topic);	
			this.updateMirrorMaker(mm);
		}
		
		orig.setTopics(whitelist);

		return orig;
		
	}

}
