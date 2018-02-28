/*
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
package org.onap.dmaap.dbcapi.model;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.System.out;
import static java.lang.System.err;

public class DmaapObjectFactory {

	/*
	 * we use localhost for most references so that connection attempts will resolve and not retry
	 * but still we expect that requests will fail.
	 */
	private static final String  fmt = "%24s: %s%n";
	private static final String	dmaap_name = "onap-ut";
	private static final String dmaap_ver = "1";
	private static final String dmaap_topic_root = "org.onap.dmaap";
	private static final String dmaap_dr = "https://localhost:8443";
	private static final String dmaap_log_url = "http://localhost:8080/log";
	private static final String dmaap_mm_topic = "org.onap.dmaap.dcae.MM_AGENT_TOPIC";
	private static final String central_loc = "SanFrancisco";
	private static final String central_layer = "central-cloud";
	private static final String central_clli = "SFCAL19240";
	private static final String central_zone = "osaz01";
	private static final String central_subnet = "10.10.10.0/24";
	private static final String central_cluster_fqdn = "localhost";
	private static final String pub_role = "org.onap.vnfapp.publisher";
	private static final String sub_role = "org.onap.vnfapp.subscriber";
	private static final String edge_loc = "Atlanta";
	private static final String edge_layer = "edge-cloud";
	private static final String edge_clli = "ATLGA10245";
	private static final String edge_zone = "osaz02";
	private static final String edge_subnet = "10.10.20.0/24";
	private static final String edge_cluster_fqdn = "localhost";
	private static final String[]hosts = { "host1", "host2", "host3" };

	public Dmaap genDmaap() {
		return new Dmaap( dmaap_ver, dmaap_topic_root, dmaap_name, dmaap_dr, dmaap_log_url, dmaap_mm_topic, "nk", "ako" );
	}

	public DcaeLocation genDcaeLocation( String layer ) {
		if ( layer.contains( "edge" ) ) {
			return new DcaeLocation( edge_clli, edge_layer, edge_loc, edge_zone, edge_subnet );
		}
		return new DcaeLocation( central_clli, central_layer, central_loc, central_zone, central_subnet );
	}


	public MR_Cluster genMR_Cluster( String layer ) {
		if ( layer.contains( "edge" ) ) {
			return new MR_Cluster( edge_loc, edge_cluster_fqdn, "ignore", hosts );
		}
		return new MR_Cluster( central_loc, central_cluster_fqdn, "ignore", hosts );
	}

	public Topic genSimpleTopic( String tname ) {
		Topic t = new Topic();
		t.setTopicName( tname );
        t.setFqtnStyle( FqtnType.Validator("none") );
        t.getFqtn();
		return t;
	}

	public MR_Client genMR_Client( String l, String f, String r, String[] a ) {
		if ( l.contains( "edge" ) ) {
			return new MR_Client( edge_loc, f, r, a );
		}
		return new MR_Client( central_loc, f, r, a );
	}

	public MR_Client genPublisher( String layer, String fqtn ) {
		String[] actions = { "pub", "view" };
		return genMR_Client( layer, fqtn, pub_role, actions );
	}
	public MR_Client genSubscriber( String layer, String fqtn ) {
		String[] actions = { "sub", "view" };
		return genMR_Client( layer, fqtn, sub_role, actions );
	}

	public DR_Sub genDrSub( String l, String feed ) {
        String un = "user1";
        String up = "secretW0rd";
        String du = "sub.server.onap.org:8443/deliver/here";
        String lu = "https://drps.onap.org:8443/sublog/123";
        boolean u100 = true;

		if ( l.contains( "edge" ) ) {
			return new DR_Sub( edge_loc, un, up, feed, du, lu, u100 );
		}
		return new DR_Sub( central_loc, un, up, feed, du, lu, u100 );
	}
				

}
