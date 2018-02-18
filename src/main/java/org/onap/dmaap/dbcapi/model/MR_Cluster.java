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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.onap.dmaap.dbcapi.util.DmaapTimestamp;



@XmlRootElement
public class MR_Cluster extends DmaapObject {

	private String dcaeLocationName;
	private String fqdn;
	private String[] hosts;
	private	DmaapTimestamp lastMod;
	private	String	topicProtocol;
	private String	topicPort;

	
	// TODO: make this a system property
	private static String defaultTopicProtocol = "https";
	private	static String defaultTopicPort = "3905";
	



	public MR_Cluster() {
		this.topicProtocol = defaultTopicProtocol;
		this.topicPort = defaultTopicPort;
		this.hosts = new String[3];
		this.lastMod = new DmaapTimestamp();
		this.lastMod.mark();

		debugLogger.debug( "MR_Cluster constructor " + this.lastMod );
		
	}
	
	public MR_Cluster( String dLN,
						String f,
						String a,
						String[] h ) {
		this.dcaeLocationName = dLN;
		this.fqdn = f;
logger.info( "templog:MR_Cluster at 10" );
		this.hosts = new String[3];
		this.hosts[0] = h[0];
logger.info( "templog:MR_Cluster at 20" );
		this.hosts[1] = h[1];
logger.info( "templog:MR_Cluster at 30" );
		this.hosts[2] = h[2];
logger.info( "templog:MR_Cluster at 40" );
		this.topicProtocol = defaultTopicProtocol;
logger.info( "templog:MR_Cluster at 50" );
		this.topicPort = defaultTopicPort;
		this.lastMod = new DmaapTimestamp();
		this.lastMod.mark();

		debugLogger.debug( "MR_Cluster constructor w initialization complete" + this.lastMod.getVal() );
logger.info( "templog:MR_Cluster at 60" );
	}

	public String getDcaeLocationName() {
		return dcaeLocationName;
	}

	public void setDcaeLocationName(String dcaeLocationName) {
		this.dcaeLocationName = dcaeLocationName;
	}

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public String getTopicProtocol() {
		return topicProtocol;
	}

	public void setTopicProtocol(String topicProtocol) {
		this.topicProtocol = topicProtocol;
	}

	public String getTopicPort() {
		return topicPort;
	}

	public void setTopicPort(String topicPort) {
		this.topicPort = topicPort;
	}



	public String genTopicURL(String overideFqdn, String topic) {

		StringBuilder str = new StringBuilder( topicProtocol );
		str.append("://")
			.append( overideFqdn != null ? overideFqdn : fqdn)
			.append(":")
			.append(topicPort)
			.append("/events/")
			.append(topic);
		
		return str.toString();


	}


}
