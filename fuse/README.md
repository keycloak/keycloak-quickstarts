JBoss Fuse Applications
=======================

Level: Beginner  
Technologies: Servlet, Apache Camel, Apache CXF  
Summary: JBoss Fuse Quickstarts  
Target Product: Keycloak, JBoss Fuse  
Source: <https://github.com/redhat-developer/redhat-sso-quickstarts>  


What is it?
-----------

This is set of quickstarts, which show various kinds of web applications and services deployed on JBoss Fuse, which are secured by Keycloak. See the individual subdirectories
for the more detailed info about particular applications and needed steps to configure and deploy them. This README contains the basic configuration steps, which 
are common for all JBoss Fuse quickstarts.


System Requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.

Start and Configure the Wildfly
-------------------------------

It is assumed that you already did the basic steps from the [Base README](../README.md) from the section "Start the Keycloak Server" .
The individual submodules will contain more specific steps for Keycloak server for individual Fuse quickstart applications. Usually it's about setup client 
and export `keycloak.json` file from it.
 
Start and Configure the JBoss Fuse
----------------------------------

* First step is to download JBoss Fuse 6.3.0. You need to download the ZIP distribution.
 
* Unzip the JBoss Fuse to some location on your filesystem.

* Start the JBoss Fuse with commands:
````
cd jboss-fuse-6.3.0.redhat-224/bin/
./fuse
````
Wait until JBoss Fuse is started and Karaf terminal shows up.

* Install the common set of Keycloak features. First use this command to set the variable with the current Keycloak version. The version can be seen
for example when you look at the [pom.xml](pom.xml) file of this quickstart and look at the content of element `<version>` .
````
RHSSO_VERSION="<YOUR-RHSSO-VERSION-HERE>"
````
For example:
````
RHSSO_VERSION="2.5.4.Final-redhat-1"
````
Then it's good to add your local maven repository to the Fuse. This is referred in following command with `file:///path/to/local/maven/repo` . 
Typically the exact location is in `file:///home/yourusername/.m2/repository` on Linux.
````
config:edit org.ops4j.pax.url.mvn
config:propset org.ops4j.pax.url.mvn.localRepository file:///path/to/local/maven/repo
config:update
````

Then you can install the features:
````
features:addurl mvn:org.keycloak/keycloak-osgi-features/$RHSSO_VERSION/xml/features
features:install keycloak
features:install keycloak-jetty9-adapter
````



Build and Deploy the Quickstarts
--------------------------------

Look at the individual subdirectories for steps needed for concrete examples.

* [app-war](app-war/README.md) - A WAR frontend application that is deployed with [pax-war extender](https://ops4j1.jira.com/wiki/display/ops4j/Pax+Web+Extender+-+War)
* [service-camel](service-camel/README.md) - [Apache Camel](http://camel.apache.org/) endpoint running on separate Jetty engine.
* [service-cxf-jaxrs](service-cxf-jaxrs/README.md) - [Apache CXF](http://cxf.apache.org/) JAX-RS endpoint running on separate Jetty engine.
* [features](features/README.md) - [Apache Karaf feature](https://karaf.apache.org/manual/latest-2.x/users-guide/provisioning.html), which simplify deploy of all the quickstarts into the JBoss Fuse at one step.
