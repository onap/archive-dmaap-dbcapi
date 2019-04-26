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

import java.util.Objects;

@XmlRootElement
public class Dmaap extends DmaapObject {
	
	private String version;
	private String topicNsRoot;
	private String dmaapName;
	private String drProvUrl;
	private	String	bridgeAdminTopic;
	private	String loggingUrl;
	private	String nodeKey;
	private	String	accessKeyOwner;


	// no-op constructor used by framework
	public Dmaap() {
		
	}
	
	public Dmaap( String ver, 
					String tnr,
					String dn,
					String dpu,
					String lu,
					String bat,
					String nk,
					String ako ) {
		this.version = ver;
		this.topicNsRoot = tnr;
		this.dmaapName = dn;
		this.drProvUrl = dpu;
		this.bridgeAdminTopic = bat;
		this.loggingUrl = lu;
		this.nodeKey = nk;
		this.accessKeyOwner = ako;
		this.setStatus( DmaapObject_Status.NEW );

	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTopicNsRoot() {
		return topicNsRoot;
	}

	public void setTopicNsRoot(String topicNsRoot) {
		this.topicNsRoot = topicNsRoot;
	}

	public String getDmaapName() {
		return dmaapName;
	}

	public void setDmaapName(String dmaapName) {
		this.dmaapName = dmaapName;
	}

	public String getDrProvUrl() {
		return drProvUrl;
	}

	public void setDrProvUrl(String drProvUrl) {
		this.drProvUrl = drProvUrl;
	}


	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public String getAccessKeyOwner() {
		return accessKeyOwner;
	}

	public void setAccessKeyOwner(String accessKeyOwner) {
		this.accessKeyOwner = accessKeyOwner;
	}

	
	public String getBridgeAdminTopic() {
		return bridgeAdminTopic;
	}

	public void setBridgeAdminTopic(String bridgeAdminTopic) {
		this.bridgeAdminTopic = bridgeAdminTopic;
	}

	public String getLoggingUrl() {
		return loggingUrl;
	}

	public void setLoggingUrl(String loggingUrl) {
		this.loggingUrl = loggingUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Dmaap dmaap = (Dmaap) o;
		return Objects.equals(version, dmaap.version) &&
				Objects.equals(topicNsRoot, dmaap.topicNsRoot) &&
				Objects.equals(dmaapName, dmaap.dmaapName) &&
				Objects.equals(drProvUrl, dmaap.drProvUrl) &&
				Objects.equals(bridgeAdminTopic, dmaap.bridgeAdminTopic) &&
				Objects.equals(loggingUrl, dmaap.loggingUrl) &&
				Objects.equals(nodeKey, dmaap.nodeKey) &&
				Objects.equals(accessKeyOwner, dmaap.accessKeyOwner);
	}

	@Override
	public int hashCode() {
		return Objects.hash(version, topicNsRoot, dmaapName, drProvUrl, bridgeAdminTopic, loggingUrl, nodeKey, accessKeyOwner);
	}

	@Override
	public String toString() {
		return "Dmaap{" +
				"version='" + version + '\'' +
				", topicNsRoot='" + topicNsRoot + '\'' +
				", dmaapName='" + dmaapName + '\'' +
				", drProvUrl='" + drProvUrl + '\'' +
				", bridgeAdminTopic='" + bridgeAdminTopic + '\'' +
				", loggingUrl='" + loggingUrl + '\'' +
				", nodeKey='" + nodeKey + '\'' +
				", accessKeyOwner='" + accessKeyOwner + '\'' +
				'}';
	}
}
