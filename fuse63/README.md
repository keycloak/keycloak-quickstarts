JBoss Fuse Applications
=======================

Level: Beginner  
Technologies: Servlet, Apache Camel, Apache CXF  
Summary: JBoss Fuse Quickstarts  
Target Product: <span>Keycloak</span>, JBoss Fuse  
Source: <https://github.com/redhat-developer/redhat-sso-quickstarts>  


What is it?
-----------

This is set of quickstarts, which show various kinds of web applications and services deployed on JBoss Fuse, which are secured by <span>Keycloak</span>. See the individual subdirectories
for the more detailed info about particular applications and needed steps to configure and deploy them. This README contains the basic configuration steps, which 
are common for all JBoss Fuse quickstarts.


System Requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.

Start and Configure the Wildfly
-------------------------------

It is assumed that you already did the basic steps from the [Base README](../README.md) from the section "Start the <span>Keycloak</span> Server" .
The individual submodules will contain more specific steps for <span>Keycloak</span> server for individual Fuse quickstart applications. Usually it's about setup client 
and export `keycloak.json` file from it.

Start and Configure the JBoss Fuse
----------------------------------
* First step is to download [JBoss Fuse 6.3.0](https://developers.redhat.com/products/fuse/download/). You need to download the ZIP distribution.

* Unzip the JBoss Fuse to some location on your filesystem.

* Setup the JBOSS_FUSE_HOME variable:

```
export JBOSS_FUSE_HOME=/path/to/jboss-fuse-6.3.0.redhat-343
```

* Set up each client based on `Build and Deploy` section

* Install the common set of Keycloak features and start the server
```
mvn clean install
```

The command above will run the server setup and deploy all the artifacts, including the services required for this quickstart. You can use any REST service deployed anywhere. However for testing purposes, it is good if you use either one (or both) of:

* [service-camel](../service-camel/README.md)
* [service-cxf-jaxrs](../service-cxf-jaxrs/README.md)

By default, `app-war` app will refer to the `service-camel` deployed at `http://localhost:8383/service`. But you can change it anytime, by running:

```
mvn clean install -Dservice.url=http://localhost:8282/service
```

If you use this command once you invoked an endpoint:
```
log:tail -n 10
```
you will see in the log which service endpoint is application trying to access. This can be useful for troubleshooting.

Build and Deploy the Quickstarts
--------------------------------

Look at the individual subdirectories for steps needed for concrete examples.

* [app-war](app-war/README.md) - A WAR frontend application that is deployed with [pax-war extender](https://ops4j1.jira.com/wiki/display/ops4j/Pax+Web+Extender+-+War)
* [service-camel](service-camel/README.md) - [Apache Camel](http://camel.apache.org/) endpoint running on separate Jetty engine.
* [service-cxf-jaxrs](service-cxf-jaxrs/README.md) - [Apache CXF](http://cxf.apache.org/) JAX-RS endpoint running on separate Jetty engine.
* [features](features/README.md) - [Apache Karaf feature](https://karaf.apache.org/manual/latest-2.x/users-guide/provisioning.html), which simplify deploy of all the quickstarts into the JBoss Fuse at one step.
