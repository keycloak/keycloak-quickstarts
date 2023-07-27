servlet-saml-service-provider: Servlet SAML Service Provider
=============================================================

Level: Beginner \
Technologies: JavaEE \
Summary: JSP Profile Application \
Target Product: <span>Keycloak</span>, <span>WildFly</span>

What is it?
-----------

This quickstart demonstrates how to protect a SAML Service Provider that authenticates using <span>Keycloak</span>. 
Once authenticated the application shows the users profile information.

System Requirements
-------------------

To compile and run this quickstart you will need:

* JDK 17
* Apache Maven 3.8.6
* Wildfly <= 23
* Keycloak 21+
* Docker 20+

Starting and Configuring the Keycloak Server
-------------------

To start a Keycloak Server you can use OpenJDK on Bare Metal, Docker, Openshift or any other option described in [Keycloak Getting Started guides]https://www.keycloak.org/guides#getting-started. For example when using Docker just run the following command in the root directory of this quickstart:

```shell
docker run --name keycloak \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  --network=host \
  quay.io/keycloak/keycloak:{KC_VERSION} \
  start-dev \
  --http-port=8180
```

where `KC_VERSION` should be set to 21.0.0 or higher.

You should be able to access your Keycloak Server at http://localhost:8180.

Log in as the admin user to access the Keycloak Administration Console. Username should be `admin` and password `admin`.

Import the [realm configuration file](config/realm-import.json) to create a new realm called `quickstart`. The easiest way to do this is using the gui. After you click on `Create Realm`, you have the option to choose a Resource JSON file.

You can also import the realm with cli.
For more details, see the Keycloak documentation about how to [import a realm using cli](https://www.keycloak.org/docs/latest/server_admin/index.html#importing-a-realm-from-exported-json-file) and [create a new realm](https://www.keycloak.org/docs/latest/server_admin/index.html#proc-creating-a-realm_server_administration_guide).

Starting the Wildfly Server
-------------------

In order to deploy the example application, you need a Wildfly Server up and running. For more details, see the Wildfly documentation about how to [install the server](https://docs.wildfly.org/).

Make sure the server is accessible from `localhost` and listening on port `8080`.

Installing the SAML 2.0 Wildfly Adapter
-----------------------------------

Install the WildFly SAML 2.0 Client Adapter following [this guide](https://www.keycloak.org/docs/latest/securing_apps/index.html#_saml-jboss-adapter-installation).

> Please consider the supported WildFly version for this adapter



Build and Deploy the Quickstart
-------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   mvn -Djakarta clean wildfly:deploy
   ````

Access the Quickstart
----------------------

You can access the application with the following URL: <http://localhost:8080/servlet-saml-service-provider>

You should be able to authenticate using any of these users:

| Username | Password | Roles              |
|----------|----------|--------------------|
| alice    | alice    | user               |

Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to undeploy the quickstart:

````
mvn -Djakarta install wildfly:undeploy
````

Running tests
--------------------

Make sure Keycloak is [running](#starting-and-configuring-the-keycloak-server).

You don't need Wildfly running because a temporary server is started during test execution.

1. Open a terminal and navigate to the root directory of this quickstart.

2. Run the following command to build and run tests:

   ````
   mvn -Djakarta clean verify
   ````

References
--------------------

* [Keycloak SAML Adapter](https://www.keycloak.org/docs/latest/securing_apps/#_saml_jboss_adapter)
* [Keycloak Documentation](https://www.keycloak.org/documentation)
