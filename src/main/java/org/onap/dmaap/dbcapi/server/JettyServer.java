/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property.
 *
 * Modifications Copyright (C) 2019 IBM.
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

package org.onap.dmaap.dbcapi.server;


import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;

import java.util.Properties;
/**
 * A  Jetty server which supports:
 * 	- http and https (simultaneously for dev env)
 *  - REST API context
 *  - static html pages (for documentation).
 */
public class JettyServer extends BaseLoggingClass {
	private Server server;


	public Server getServer() {
		return server;
	}

    public JettyServer( Properties params ) throws Exception {

        server = new Server();
    	int httpPort = Integer.valueOf(params.getProperty("IntHttpPort", "80" ));
       	int sslPort = Integer.valueOf(params.getProperty("IntHttpsPort", "443" ));
       	boolean allowHttp = Boolean.valueOf(params.getProperty("HttpAllowed", "false"));
    	serverLogger.info( "port params: http=" + httpPort + " https=" + sslPort );
    	serverLogger.info( "allowHttp=" + allowHttp );

		// HTTP Server
    	HttpConfiguration http_config = new HttpConfiguration();
    	http_config.setSecureScheme("https");
    	http_config.setSecurePort(sslPort);
    	http_config.setOutputBufferSize(32768);

    	
    	
        try(ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(http_config))) {
			httpConnector.setPort(httpPort);
			httpConnector.setIdleTimeout(30000);


			// HTTPS Server

			HttpConfiguration https_config = new HttpConfiguration(http_config);
			https_config.addCustomizer(new SecureRequestCustomizer());
			SslContextFactory sslContextFactory = new SslContextFactory();

			setUpKeystore(params, sslContextFactory);
			setUpTrustStore(params, sslContextFactory);

			if (sslPort != 0) {
                try(ServerConnector sslConnector = new ServerConnector(server,
						new SslConnectionFactory(sslContextFactory, "http/1.1"),
						new HttpConnectionFactory(https_config))) {
                    sslConnector.setPort(sslPort);
                    if (allowHttp) {
                        logger.info("Starting httpConnector on port " + httpPort);
                        logger.info("Starting sslConnector on port " + sslPort + " for https");
                        server.setConnectors(new Connector[]{httpConnector, sslConnector});
                    } else {
                        logger.info("NOT starting httpConnector because HttpAllowed param is " + allowHttp);
                        logger.info("Starting sslConnector on port " + sslPort + " for https");
                        server.setConnectors(new Connector[]{sslConnector});
                    }
                }
			} else {
				serverLogger.info("NOT starting sslConnector on port " + sslPort + " for https");
				if (allowHttp) {
					serverLogger.info("Starting httpConnector on port " + httpPort);
					server.setConnectors(new Connector[]{httpConnector});
				}
			}
		}

        // Set context for servlet.  This is shared for http and https
       	ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    	context.setContextPath("/");
        server.setHandler( context );

        ServletHolder jerseyServlet = context.addServlet( org.glassfish.jersey.servlet.ServletContainer.class, "/webapi/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "org.onap.dmaap.dbcapi.resources" );   
        jerseyServlet.setInitParameter("javax.ws.rs.Application", "org.onap.dmaap.dbcapi.server.ApplicationConfig" );
        
        // also serve up some static pages...
        ServletHolder staticServlet = context.addServlet(DefaultServlet.class,"/*");
        staticServlet.setInitParameter("resourceBase","www");
        staticServlet.setInitParameter("pathInfoOnly","true");

        try {

            serverLogger.info("Starting jetty server");
        	String unit_test = params.getProperty("UnitTest", "No");
            serverLogger.info("UnitTest=" + unit_test);
			if ( unit_test.equals( "No" ) ) {
        		server.start();
        		server.dumpStdErr();
            	server.join();
			}
        } catch ( Exception e ) {
        	errorLogger.error( "Exception " + e );
        } finally {
        	server.destroy();
        }
        
    }

	private void setUpKeystore(Properties params, SslContextFactory sslContextFactory) {
		String keystore = params.getProperty("KeyStoreFile", "etc/keystore");
		logger.info("https Server using keystore at " + keystore);
		sslContextFactory.setKeyStorePath(keystore);
		sslContextFactory.setKeyStorePassword(params.getProperty("KeyStorePassword", "changeit"));
		sslContextFactory.setKeyManagerPassword(params.getProperty("KeyPassword", "changeit"));
	}

	private void setUpTrustStore(Properties params, SslContextFactory sslContextFactory) {
		String truststore = params.getProperty("TrustStoreFile", "etc/org.onap.dmaap-bc.trust.jks");
		logger.info("https Server using truststore at " + truststore);
		sslContextFactory.setTrustStorePath(truststore);
		sslContextFactory.setTrustStoreType(params.getProperty("TrustStoreType", "jks"));
		sslContextFactory.setTrustStorePassword(params.getProperty("TrustStorePassword", "changeit"));
	}
}
