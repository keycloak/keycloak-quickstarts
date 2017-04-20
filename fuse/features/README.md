JBoss Fuse Features
===================
This module contains the Karaf feature `keycloak-fuse-quickstarts`, which can be used to install all the other RH-SSO Fuse Quickstart 
applications into the JBoss Fuse server. 

Build and Deploy quickstarts
----------------------------
1) You need to individually setup and configure every RH-SSO quickstart separately as mentioned in the README of individual quickstarts.
This typically means the setup on the RH-SSO server side, exporting `keycloak.json` file from RH-SSO server and finally build every quickstart with
`mvn clean install`. See the README of the individual quickstarts for more details:
* [app-war](../app-war/README.md)
* [service-camel](../service-camel/README.md)
* [service-cxf-jaxrs](../service-cxf-jaxrs/README.md)

2) Build this module with maven:
````
mvn clean install
````

3) Run the command in JBoss Fuse Karaf terminal to build all features and their dependencies (See [parent README](../README.md) for 
more info about `$RHSSO_VERSION` variable:

````
features:addurl mvn:com.redhat.rh-sso/rh-sso-fuse-features/$RHSSO_VERSION/xml/features
features:install keycloak-fuse-quickstarts
````

Now all the RH-SSO quickstarts are installed (deployed) and you can fully test them. When you want to uninstall (undeploy) all the 
quickstarts again, you can use:

````
features:uninstall keycloak-fuse-quickstarts
````