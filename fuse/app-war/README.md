app-war-jsp: Servlet and JSP application packed as WAR 
======================================================

Level: Beginner  
Technologies: Servlet, JSP  
Summary: Servlet and JSP application  
Target Product: RH-SSO, JBoss Fuse  
Source: <https://github.com/redhat-developer/redhat-sso-quickstarts>  


What is it?
-----------

The `app-war-jsp` quickstart demonstrates how to write a Servlet JSP application, which is packaged as a WAR. The application will be secured by RH-SSO and deployed to JBoss Fuse.

The application provides simple UI where you can login/logout and see Account management built-in RH-SSO page. Also it allows 
you to invoke service REST endpoint, which will be typically provided either by Apache Camel or Apache CXF service quickstarts.


System Requirements
-------------------

You need to have JBoss Fuse 6.3.0

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in RH-SSO
-----------------------

Prior to running the quickstart you need to create a client in RH-SSO and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the RH-SSO admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `fuse-app-jsp`)
  * Client Protocol: `openid-connect`
  * Root URL: URL to the application (for example `http://localhost:8181/app-war-jsp`)
* Click `Save`

Once saved you need to change the Access Type to `confidential` and click `Save`.

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the directory `src/main/webapp/WEB-INF/`in the quickstart

As an alternative you can create the client by importing the file [client-import.json](config/client-import.json) and
copying [config/keycloak-example.json](config/keycloak-example.json) to `src/main/webapp/WEB-INF/keycloak.json`.

Build the Quickstart
--------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. Build the quickstart with:
````
mvn clean install
````

Deploy the Quickstart
---------------------
There are 2 alternatives:
- Deploy as the Karaf feature together with other Fuse quickstarts. See [Features README](../features/README.md) for more details.
- Deploy separately. This can be done by running those commands in JBoss Fuse Karaf terminal (See [parent README](../README.md) for details about RHSSO_VERSION variable) :

````
osgi:install mvn:com.redhat.rh-sso/rh-sso-fuse-app-war-jsp/$RHSSO_VERSION/war
````
Command will output Bundle ID. For example:
````
Bundle ID: 437
````
You will use this as the input to the next command:
````
osgi:start 437
````

Access the Quickstart
---------------------

You can access the application with the following URL: <http://localhost:8181/app-war-jsp>.

The application provides buttons that allows invoking the different endpoints on the service:

* Invoke public - Invokes the public endpoint and doesn't require a user to be logged-in
* Invoke secured - Invokes the secured endpoint and requires a user with the role `user` to be logged-in
* Invoke admin - Invokes the secured endpoint and requires a user with the role `admin` to be logged-in

If you invoke the endpoints without the required permissions an error will be shown.

However there is also one important step, that first you need to build and deploy REST service, which those endpoints will be invoked against.
You can use any REST service deployed anywhere. However for testing purposes, it is good if you use either one (or both) of:

* [service-camel](../service-camel/README.md)
* [service-cxf-jaxrs](../service-cxf-jaxrs/README.md)

So once you have at least one of those services deployed in the JBoss Fuse, you can configure the application to invoke against them.

The service URL of the REST service can be changed via the system property `service.url` . The Apache Camel service is deployed on `http://localhost:8383/service` by default. 
So if you run this command in the JBoss Fuse Karaf terminal:
````
dev:system-property service.url http://localhost:8383/service
````
the application will try to invoke Apache Camel service. The Apache CXF JAXRS service is deployed on `http://localhost:8282/service` by default. 
So if you run this command in the JBoss Fuse Karaf terminal:
````
dev:system-property service.url http://localhost:8282/service
````
the application will try to invoke Apache CXF service.

If you use this command once you invoked an endpoint:
````
log:tail -n 10
````
you will see in the log which service endpoint is application trying to access. This can be useful for troubleshooting.


Undeploy the Quickstart
-----------------------
If you used Karaf feature, then see [Features README](../features/README.md) for more details. Otherwise use those commands (again replace 
with the proper bundle ID):
````
osgi:stop 437
osgi:uninstall 437
````

