app-war-jsp: Servlet and JSP application packed as WAR
======================================================

Level: Beginner
Technologies: Servlet, JSP
Summary: Servlet and JSP application
Target Product: Keycloak, JBoss Fuse 7
Source: <https://github.com/redhat-developer/redhat-sso-quickstarts>


What is it?
-----------

The `app-war-jsp` quickstart demonstrates how to write a Servlet JSP application, which is packaged as a WAR. The application will be secured by Keycloak and deployed to JBoss Fuse 7.

The application provides simple UI where you can login/logout and see Account management built-in Keycloak page. Also it allows
you to invoke service REST endpoint, which will be typically provided either by Apache Camel or Apache CXF service quickstarts.


System Requirements
-------------------

You need to have JBoss Fuse 7.0.1 or newer

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in Keycloak
-----------------------

Prior to running the quickstart you need to create a client in Keycloak and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the Keycloak admin console
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
* Select `<span>Keycloak</span> OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the `config/` directory in the root of the quickstart

As an alternative you can create the client by importing the file [client-import.json](config/client-import.json) and
copying [config/keycloak-example.json](config/keycloak-example.json) to `config/keycloak.json`.

Build the Quickstart
--------------------

1. Open a terminal and navigate to the [fuse70](..) directory.

```
mvn clean install
```

Access the Quickstart
---------------------

You can access the application with the following URL: <http://localhost:8181/app-war-jsp>.

The application provides buttons that allows invoking the different endpoints on the service:

* Invoke public - Invokes the public endpoint and doesn't require a user to be logged-in
* Invoke secured - Invokes the secured endpoint and requires a user with the role `user` to be logged-in
* Invoke admin - Invokes the secured endpoint and requires a user with the role `admin` to be logged-in

If you invoke the endpoints without the required permissions an error will be shown.
