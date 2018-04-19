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

import javax.xml.bind.annotation.XmlRootElement;

import org.onap.dmaap.dbcapi.util.DmaapTimestamp;



@XmlRootElement
public class MR_Cluster extends DmaapObject {

	private String dcaeLocationName;
	private String fqdn;
	private	DmaapTimestamp lastMod;
	private	String	topicProtocol;
	private String	topicPort;

	
	// TODO: make this a system property
	private static String defaultTopicProtocol = "https";
	private	static String defaultTopicPort = "3905";
	



	public MR_Cluster() {
		this.topicProtocol = defaultTopicProtocol;
		this.topicPort = defaultTopicPort;
		this.lastMod = new DmaapTimestamp();
		this.lastMod.mark();

		debugLogger.debug( "MR_Cluster constructor " + this.lastMod );
		
	}
	

	
	// new style constructor
	public MR_Cluster( String dLN,
			String f,
			String prot,
			String port ) {
		this.dcaeLocationName = dLN;
		this.fqdn = f;

		if ( prot == null || prot.isEmpty() ) {
			this.topicProtocol = defaultTopicProtocol;
		} else {
			this.topicProtocol = prot;
		}
		if ( port == null || port.isEmpty() ) {
			this.topicPort = defaultTopicPort;
		} else {
			this.topicPort = port;
		}
		
		
		this.lastMod = new DmaapTimestamp();
		this.lastMod.mark();
		
		debugLogger.debug( "MR_Cluster constructor w initialization complete" + this.lastMod.getVal() );
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
