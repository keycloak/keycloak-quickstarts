jakarta-jaxrs-resource-server: JAX-RS Resource Server
===================================================

Level: Beginner
Technologies: Jakarta EE
Summary: A JAX-RS resource server protected with Wildfly Elytron OIDC
Target Product: <span>Keycloak</span>, <span>WildFly</span>

What is it?
-----------

This quickstart demonstrates how to write a RESTful service with Jakarta RESTful Web Service that is secured with <span>Keycloak</span>.

The endpoints are very simple and will only return a simple message stating what endpoint was invoked.

System Requirements
-------------------

To compile and run this quickstart you will need:

* JDK 17
* Apache Maven 3.8.6
* Wildfly 28+
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

Import the [realm configuration file](config/realm-import.json) to create a new realm called `quickstart`.
For more details, see the Keycloak documentation about how to [create a new realm](https://www.keycloak.org/docs/latest/server_admin/index.html#_create-realm).

Starting the Wildfly Server
-------------------

In order to deploy the example application, you need a Wildfly Server up and running. For more details, see the Wildfly documentation about how to [install the server](https://docs.wildfly.org/).

Make sure the server is accessible from `localhost` and listening on port `8080`.

Build and Deploy the Quickstart
-------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   mvn -Djakarta clean wildfly:deploy
   ````

Access the Quickstart
---------------------

There are 3 endpoints exposed by the service:

* http://localhost:8080/service/public - requires no authentication
* http://localhost:8080/service/secured - can be invoked by users with the `user` role
* http://localhost:8080/service/admin - can be invoked by users with the `admin` role

You can open the public endpoint directly in the browser to test the service. The two other endpoints are protected and require
invoking them with a bearer token.

To invoke the protected endpoints using a bearer token, your client needs to obtain an OAuth2 access token from a Keycloak server.
In this example, we are going to obtain tokens using the resource owner password grant type so that the client can act on behalf of any user available from
the realm.

You should be able to obtain tokens for any of these users:

| Username | Password | Roles              |
|----------|----------|--------------------|
| alice    | alice    | user               |
| admin    | admin    | admin              |

To obtain the bearer token, run the following command:

```shell
export access_token=$(\
curl -X POST http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
-H 'content-type: application/x-www-form-urlencoded' \
-d 'client_id=jakarta-jaxrs-resource-server&client_secret=secret' \
-d 'username=alice&password=alice&grant_type=password' | jq --raw-output '.access_token' \
)
```

You can use the same command to obtain tokens on behalf of user `admin`, just make sure to change both `username` and `password` request parameters.

After running the command above, you can now access the `http://localhost:8080/service/secured` endpoint
because the user `alice` has the `user` role.

```shell
curl http://localhost:8080/jakarta-jaxrs-resource-server/secured \
  -H "Authorization: Bearer "$access_token
```

As a result, you will see the following response from the service:

```json
{"message":"secured"}
```

You may also want to enable CORS for the service if you want to allow invocations from HTML5 applications deployed to a
different host. To do this edit [oidc.json](src/main/webapp/WEB-INF/oidc.json) file and add:

````
{
   ...
   "enable-cors": true
}
````

Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   mvn -Djakarta clean wildfly:undeploy
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

* [Wildfly Elytron OpenID Connect](https://docs.wildfly.org/28/Admin_Guide.html#Elytron_OIDC_Client)
* [Keycloak Documentation](https://www.keycloak.org/documentation)