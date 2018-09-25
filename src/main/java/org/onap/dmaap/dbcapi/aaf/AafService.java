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

package org.onap.dmaap.dbcapi.aaf;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

/*
 * this service uses the AAF REST API endpoints to provision values in AAF
 */
public class AafService extends BaseLoggingClass {
	public enum ServiceType {
		AAF_Admin,
		AAF_TopicMgr
	}
	
	private AafConnection aaf;
	private ServiceType ctype;
	private String aafURL ;
	private boolean useAAF = false;
	
	
	private String getCred( boolean wPwd ) {
		String mechIdProperty = null;
		String pwdProperty = null;
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		AafDecrypt decryptor = new AafDecrypt();

		if ( ctype == ServiceType.AAF_Admin ) {
			 mechIdProperty = "aaf.AdminUser";
			 pwdProperty = "aaf.AdminPassword";
		} else if ( ctype == ServiceType.AAF_TopicMgr ){	
			 mechIdProperty = "aaf.TopicMgrUser";
			 pwdProperty = "aaf.TopicMgrPassword";
		} else {
			logger.error( "Unexpected case for AAF credential type: " + ctype );
			return null;
		}
		String user = p.getProperty( mechIdProperty, "noMechId@domain.netset.com" );

		String pwd = "";
		String encPwd = p.getProperty( pwdProperty, "notSet" );

		
		pwd = decryptor.decrypt(encPwd);
		
		if ( wPwd ) {
			return user + ":" + pwd;
		} else {
			return user;
		}
		
		
	}
	
	public AafService(ServiceType t ) {
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		aafURL = p.getProperty( "aaf.URL", "https://authentication.domain.netset.com:8095/proxy/");
		initAafService( t );
	}
	public AafService( ServiceType t, String url ) {
		aafURL = url;
		initAafService( t );
	}
		
	private void initAafService( ServiceType t ) {
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		useAAF= "true".equalsIgnoreCase(p.getProperty("UseAAF", "false"));
		logger.info( "AafService initAafService: useAAF=" + useAAF);
		
		ctype = t;
		aaf = new AafConnection( getCred( true ) );
	}
	
	public int addPerm(DmaapPerm perm) {

		int rc = -1;
		logger.info( "entry: addPerm() "  );
		String pURL = aafURL + "authz/perm";
		logger.info( "addPerm=" + useAAF );
		if ( useAAF ) {
			logger.info( "addPerm: " + perm.toJSON());
			rc = aaf.postAaf( perm, pURL );
		} else {
			rc = 201;
		}
        switch( rc ) {
    	case 401:
    	case 403:
       		errorLogger.error(DmaapbcLogMessageEnum.AAF_CREDENTIAL_ERROR,  getCred( false ) );
    		System.exit(1);
    	case 409:
    		logger.warn( "Perm already exists. Possible conflict.");
    		break;
 		
    	case 201:
    		logger.info( "expected response: " + rc);
    		break;
       	default :
    		logger.error( "Unexpected response: " + rc );
    		break;
        }
		
		return rc;
	}
	public int addGrant(DmaapGrant grant ) {

		int rc = -1;
		logger.info( "entry: addGrant() "  );

		String pURL = aafURL + "authz/role/perm";
		logger.info( "addGrant: useAAF=" + useAAF );
		if ( useAAF ) {
			logger.info( "addGrant: " + grant.toJSON() );
			rc = aaf.postAaf( grant, pURL );
		} else {
			rc = 201;
		}
		
        switch( rc ) {
    	case 401:
    	case 403:
       		errorLogger.error(DmaapbcLogMessageEnum.AAF_CREDENTIAL_ERROR,  getCred( false ) );
    		System.exit(1);
    		break;

    	case 409:
    		logger.warn( "Perm already exists. Possible conflict.");
    		break;
 		
    	case 201:
    		logger.info( "expected response" );
    		break;
       	default :
    		logger.error( "Unexpected response: " + rc );
    		break;
        }
		
		return rc;
	}

	public int delGrant( DmaapGrant grant ) {
		int rc = -1;
		logger.info( "entry: delGrant() "  );

		String pURL = aafURL + "authz/role/:" + grant.getRole() + "/perm";
		
		if ( useAAF ) {
			rc = aaf.delAaf( grant, pURL );
		} else {
			rc = 200;
		}
        switch( rc ) {
    	case 401:
       	case 403:
     		errorLogger.error(DmaapbcLogMessageEnum.AAF_CREDENTIAL_ERROR,  getCred( false ) );
    		System.exit(1);
    		break;
 
    	case 404:
    		logger.warn( "Perm not found...ignore");
    		break;
 		
    	case 200:
    		logger.info( "expected response" );
    		break;
       	default :
    		logger.error( "Unexpected response: " + rc );
    		break;
        }
		
		return rc;
	}


}
