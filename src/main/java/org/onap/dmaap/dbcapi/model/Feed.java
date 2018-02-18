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

package org.onap.dmaap.dbcapi.model;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.*;
import org.json.simple.parser.*;
import org.onap.dmaap.dbcapi.service.DmaapService;

@XmlRootElement
public class Feed extends DmaapObject {
		
		private String feedId;

		private String feedName;
		private String feedVersion;
		private String feedDescription;
		private String owner;
		private String asprClassification;
		private String publishURL;
		private String subscribeURL;
		private	boolean	suspended;
		private String logURL;
		private String formatUuid;

		private	ArrayList<DR_Pub> pubs;
		private ArrayList<DR_Sub> subs;	

		

		public boolean isSuspended() {
			return suspended;
		}

		public void setSuspended(boolean suspended) {
			this.suspended = suspended;
		}

		public String getSubscribeURL() {
			return subscribeURL;
		}

		public void setSubscribeURL(String subscribeURL) {
			this.subscribeURL = subscribeURL;
		}


		
		public Feed() {
			this.pubs = new ArrayList<DR_Pub>();
			this.subs = new ArrayList<DR_Sub>();
			this.setStatus( DmaapObject_Status.EMPTY );
			
		}
		
		public	Feed( String name,
					String version,
					String description,
					String owner,
					String aspr
					 ) {
			this.feedName = name;
			this.feedVersion = version;
			this.feedDescription = description;
			this.owner = owner;
			this.asprClassification = aspr;
			this.pubs = new ArrayList<DR_Pub>();
			this.subs = new ArrayList<DR_Sub>();
			this.setStatus( DmaapObject_Status.NEW );
			
		}
		
		// expects a String in JSON format, with known fields to populate Feed object
		public Feed ( String json ) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
		logger.info( "templog:Feed at 10" );	
			try {
				jsonObj = (JSONObject) parser.parse( json );
			} catch ( ParseException pe ) {
	            logger.error( "Error parsing provisioning data: " + json );
	            this.setStatus( DmaapObject_Status.INVALID );
	            return;
	        }
		logger.info( "templog:Feed at 11" );	
			this.setFeedName( (String) jsonObj.get("name"));

		logger.info( "templog:Feed at 12" );	

			this.setFeedVersion( (String) jsonObj.get("version"));
		logger.info( "templog:Feed at 13" );	
			this.setFeedDescription( (String) jsonObj.get("description"));
		logger.info( "templog:Feed at 14" );	
			this.setOwner( (String) jsonObj.get("publisher"));
		logger.info( "templog:Feed at 15" );	

			this.setSuspended( (boolean) jsonObj.get("suspend"));
		logger.info( "templog:Feed at 16" );	
			JSONObject links = (JSONObject) jsonObj.get("links");
		logger.info( "templog:Feed at 17" );	
			String url = (String) links.get("publish");
		logger.info( "templog:Feed at 18" );	
			this.setPublishURL( url );
		logger.info( "templog:Feed at 19" );	
			this.setFeedId( url.substring( url.lastIndexOf('/')+1, url.length() ));
		logger.info( "templog:Feed at 20" );	
			logger.info( "feedid="+ this.getFeedId() );
		logger.info( "templog:Feed at 21" );	
			this.setSubscribeURL( (String) links.get("subscribe") );					
		logger.info( "templog:Feed at 22" );	
			this.setLogURL( (String) links.get("log") );
		logger.info( "templog:Feed at 23" );	
			JSONObject auth = (JSONObject) jsonObj.get("authorization");
		logger.info( "templog:Feed at 24" );	
			this.setAsprClassification( (String) auth.get("classification"));
		logger.info( "templog:Feed at 25" );	
			JSONArray pubs = (JSONArray) auth.get( "endpoint_ids");
		logger.info( "templog:Feed at 26" );	
			int i;
		logger.info( "templog:Feed at 27" );	
			ArrayList<DR_Pub> dr_pub = new ArrayList<DR_Pub>();
		logger.info( "templog:Feed at 28" );	
			this.subs = new ArrayList<DR_Sub>();

			for( i = 0; i < pubs.size(); i++ ) {
		logger.info( "templog:Feed at 29 " + i  );	
				JSONObject entry = (JSONObject) pubs.get(i);
		logger.info( "templog:Feed at 30" );	
				dr_pub.add(  new DR_Pub( "someLocation", 
									(String) entry.get("id"),
									(String) entry.get("password"),
									this.getFeedId(),
									this.getFeedId() + "." +  DR_Pub.nextKey() ));
			
			}
		logger.info( "templog:Feed at 31" );	
			this.setPubs( dr_pub );
	
		logger.info( "templog:Feed at 32" );	
			this.setStatus( DmaapObject_Status.VALID );
		logger.info( "templog:Feed at 33" );	

		}

		public String getFeedId() {
			return feedId;
		}

		public void setFeedId(String feedId) {
			this.feedId = feedId;
		}

		public String getFeedName() {
			return feedName;
		}

		public void setFeedName(String feedName) {
			this.feedName = feedName;
		}

		public String getFeedVersion() {
			return feedVersion;
		}

		public void setFeedVersion(String feedVersion) {
			this.feedVersion = feedVersion;
		}

		public String getFeedDescription() {
			return feedDescription;
		}

		public void setFeedDescription(String feedDescription) {
			this.feedDescription = feedDescription;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getAsprClassification() {
			return asprClassification;
		}

		public void setAsprClassification(String asprClassification) {
			this.asprClassification = asprClassification;
		}

		public String getPublishURL() {
			return publishURL;
		}

		public void setPublishURL(String publishURL) {
			this.publishURL = publishURL;
		}

		public String getLogURL() {
			return logURL;
		}

		public void setLogURL(String logURL) {
			this.logURL = logURL;
		}


		
		public String getFormatUuid() {
			return formatUuid;
		}

		public void setFormatUuid(String formatUuid) {
			this.formatUuid = formatUuid;
		}

		// returns the Feed object in JSON that conforms to DR Prov Server expectations
		public String toProvJSON() {

			ArrayList<DR_Pub> pubs = this.getPubs();
			String postJSON = String.format("{\"name\": \"%s\", \"version\": \"%s\", \"description\": \"%s\", \"suspend\": %s, \"authorization\": { \"classification\": \"%s\", ",  
					this.getFeedName(), 
					this.getFeedVersion(),
					this.getFeedDescription(),
					this.isSuspended() ,
					this.getAsprClassification()
					);
			int i;
			postJSON += "\"endpoint_addrs\": [],\"endpoint_ids\": [";
			String comma = "";
			for( i = 0 ; i < pubs.size(); i++) {
				postJSON +=	String.format("	%s{\"id\": \"%s\",\"password\": \"%s\"}", 
						comma,
						pubs.get(i).getUsername(),
						pubs.get(i).getUserpwd()
						) ;
				comma = ",";
			}
			postJSON += "]}}";
			
			logger.info( "postJSON=" + postJSON);		
			return postJSON;
		}
		
		public ArrayList<DR_Pub> getPubs() {
			return pubs;
		}

		public void setPubs( ArrayList<DR_Pub> pubs) {
			this.pubs = pubs;
		}

		public ArrayList<DR_Sub> getSubs() {
			return subs;
		}

		public void setSubs( ArrayList<DR_Sub> subs) {
			this.subs = subs;
		}

		public byte[] getBytes() {
			return toProvJSON().getBytes(StandardCharsets.UTF_8);
		}
		
		public static String getSubProvURL( String feedId ) {
			String ret = new String();
			ret = new DmaapService().getDmaap().getDrProvUrl() + "/subscribe/" + feedId ;
			return ret;
		}

		@Override
		public String toString() {
			String rc = String.format ( "Feed: {feedId=%s feedName=%s feedVersion=%s feedDescription=%s owner=%s asprClassification=%s publishURL=%s subscriberURL=%s suspended=%s logURL=%s formatUuid=%s}",
					feedId,
					feedName,
					feedVersion,
					feedDescription,
					owner,
					asprClassification,
					publishURL,
					subscribeURL,
					suspended,
					logURL,
					formatUuid

		
					);

			for( DR_Pub pub: pubs) {
				rc += "\n" + pub.toString();
			}

			for( DR_Sub sub: subs ) {
				rc += "\n" + sub.toString();
			}
			return rc;
		}
}
