DMaaP Bus Controller API
=======================

Data Movement as a Platform (DMaaP) Bus Controller provides an API for other ONAP infrastructure components to provision DMaaP resources.
A typical DMaaP resource is a Data Router Feed or a Message Router Topic, and their associated publishers and subscribers.
Other infrastucture resources such as DR Nodes and MR Clusters are also provisioned through this API.

### Build Instructions for a Continuous Integration environment using Jenkins

When this component is included in a Continuous Integration environment, such as structured by the Linux Foundation, the artifacts can be created and deployed via Jenkins.  The following maven targets are currently supported in the Build step:
```
clean install
javadoc:javadoc
sonar:sonar
```

### Build Instructions for external developers

This project is organized as a mvn project for a jar package.
After cloning from this git repo:

```
mvn clean install javadoc:javadoc
```

### Configurable Parameters

Behavior of the API is controlled by settings in a properties file (typically etc/dmaapbc.properties).
The following describes these properties:

```

#
#	Configuration parameters fixed at startup for the DMaaP Bus Controller
#
#
#	URI to retrieve dynamic DR configuration
#
ProvisioningURI:	/internal/prov
#
#	Allow http access to API 
#
HttpAllowed:	true
#
#	The port number for http as seen within the server
#
IntHttpPort:	8080
#
#	The port number for https as seen within the server
#   Set to 0 if no certificate is available yet...
#
IntHttpsPort:	8443
#
#	The external port number for https taking port mapping into account
#
ExtHttpsPort:	443
#
#	The type of keystore for https
#
KeyStoreType:	jks
#
#	The path to the keystore for https
#
KeyStoreFile:	etc/keystore
#
#	The password for the https keystore
#
KeyStorePassword:	changeit
#
#	The password for the private key in the https keystore
#
KeyPassword:	changeit
#
#	The type of truststore for https
#
TrustStoreType:	jks
#
#	The path to the truststore for https
#
TrustStoreFile:	/opt/app/java/jdk/jdk180/jre/security/cacerts
#
#	The password for the https truststore
#
TrustStorePassword:	changeit
#
#	The path to the file used to trigger an orderly shutdown
#
QuiesceFile:	etc/SHUTDOWN
#
#	Enable postgress
#
UsePGSQL:	true
#
#	The host for postgres access
#
DB.host:	HostNotSet
#
#	For postgres access
#
DB.cred:	ValueNotSet
#
#	Name of this environment
#
DmaapName:	DeploymentEnvName
#
#	Name of DR prov server
#
DR.provhost:	dcae-drps.domain.notset.com
#
#	The Role and credentials of the MirrorMaker Provisioner.  This is used by DMaaP Bus Controller to pub to the provisioning topic
#   Not part of 1701
#
#MM.ProvRole: org.openecomp.dmaapBC.MMprov.prov
#MM.ProvUserMechId: idNotSet@namespaceNotSet
#MM.ProvUserPwd: enc:fMxh-hzYZldbtyXumQq9aJU08SslhbM6mXtt
#
#	The Role of the MirrorMaker Agent. This is used by MM to sub to provisioning topic
#
MM.AgentRole: org.openecomp.dmaapBC.MMagent.agent
#################
# AAF Properties:
#
# regarding password encryption:
# In the dependencies that Maven retrieves (e.g., under dcae_dmaapbc/target/deps/ is a jar file cadi-core-version.jar.  Generate the key file with:
#
# java \u2013jar wherever/cadi-core-*.jar keygen keyfilename
# chmod 400 keyfilename
#
# To encrypt a key:
#
# java \u2013jar wherever/cadi-core-*.jar digest password-to-encrypt keyfilename
#
# This will generate a string.  Put \u201Cenc:\u201D on the front of the string, and put the result in this properties file.
#
# Location of the Codec Keyfile which is used to decrypt passwords in this properties file before they are passed to AAF
#
# REF: https://wiki.domain.notset.com/display/cadi/CADI+Deployment
#
CredentialCodecKeyfile:	etc/LocalKey
#
# URL of AAF environment to use.
#
aaf.URL:	https://authentication.simpledemo.openecomp.org:8095/proxy/
#
# TopicMgr mechid@namespace
#
aaf.TopicMgrUser:	idNotSet@namespaceNotSet
#
# TopicMgr password
# 
aaf.TopicMgrPassword:	enc:zyRL9zbI0py3rJAjMS0dFOnYfEw_mJhO
#
# Bus Controller Namespace Admin  mechid@namespace
#
aaf.AdminUser:	idNotSet@namespaceNotSet
#
# Bus Controller Namespace Admin password
#
aaf.AdminPassword:	enc:YEaHwOJrwhDY8a6usetlhbB9mEjUq9m
#
# endof AAF Properties
#################
#################
# PolicyEngine Properties
#
# Flag to turn on/off Authentication
UsePE: false
#
# Argument to decisionAttributes.put("AAF_ENVIRONMENT", X); 
# where X is:  TEST= UAT, PROD = PROD, DEVL = TEST
#
PeAafEnvironment: DEVL
#
# Name of PolicyEngineApi properties file
PolicyEngineProperties: config/PolicyEngineApi.properties
#
# Namespace for URI values for API used to create AAF permissions
# e.g. if ApiNamespace is X.Y..dmaapBC.api then for URI /topics we create an AAF perm X.Y..dmaapBC.api.topics
ApiNamespace: org.onap.dmaap.dbcapi
#
# endof PolicyEngineProperties
#################

```



