rest-authz-resource-server: Spring Boot REST Service Protected Using Keycloak Authorization Services
===================================================

Level: Beginner
Technologies: Spring Boot
Summary: Spring Boot REST Service Protected Using Keycloak Authorization Services
Target Product: Keycloak

What is it?
-----------

This quickstart demonstrates how to protect a Spring Boot REST service using Keycloak Authorization Services.

It tries to focus on the authorization features provided by Keycloak Authorization Services, where resources are
protected by a set of permissions and policies defined in Keycloak and access to these resources are enforced by a policy enforcer(PEP)
that intercepts every single request sent to the application to check whether or not access should be granted.

System Requirements
-------------------

To compile and run this quickstart you will need:

* JDK 17
* Apache Maven 3.8.6
* Spring Boot 3.0.6
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

Build and Run the Quickstart
-------------------------------

If your server is up and running, perform the following steps to start the application:

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to run the application:

   ````
   mvn spring-boot:run

   ````

Access the Quickstart
---------------------

There are 2 endpoints exposed by the service:

* http://localhost:8080/ - can be invoked by any authenticated user
* http://localhost:8080/protected/premium - can be invoked by users with the `user_premium` role

To invoke the protected endpoints using a bearer token, your client needs to obtain an OAuth2 access token from a Keycloak server.
In this example, we are going to obtain tokens using the resource owner password grant type so that the client can act on behalf of any user available from
the realm.

You should be able to obtain tokens for any of these users:

| Username | Password | Roles        |
|----------|----------|--------------|
| jdoe     | jdoe     | user_premium |
| alice    | alice    | user         |

To obtain the bearer token, run the following command:

```shell
export access_token=$(\
curl -X POST http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
-H 'content-type: application/x-www-form-urlencoded' \
-d 'client_id=authz-servlet&client_secret=secret' \
-d 'username=jdoe&password=jdoe&grant_type=password' | jq --raw-output '.access_token' \
)
```

You can use the same command to obtain tokens on behalf of user `alice`, just make sure to change both `username` and `password` request parameters.

After running the command above, you can now access the `http://localhost:8080/protected/premium` endpoint
because the user `jdoe` has the `user_premium` role.

```shell
curl http://localhost:8080/protected/premium \
  -H "Authorization: Bearer "$access_token
```

As a result, you will see the following response from the service:

```
Hello, jdoe!
```

Accessing Protected Resources using Requesting Party Token (RPT)
---------------------

Another approach to access resources protected by a policy enforcer is using a RPT as a bearer token, instead of a regular access token. 
The RPT is an access token with all permissions granted by the server, basically, an access token containing all permissions granted by the server.

To obtain an RPT, you must first exchange an OAuth2 Access Token for a RPT by invoking the token endpoint at the Keycloak server: 

```bash
export rpt=$(curl -X POST \
 http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
 -H "Authorization: Bearer "$access_token \
 --data "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
 --data "audience=authz-servlet" \
  --data "permission=Premium Resource" | jq --raw-output '.access_token' \
 )
```

The command above is trying to obtain permissions from the server in the format of a RPT. Note that the request is specifying the resource we want
to obtain permissions, in this case, `Premium Resource`.

As an alternative, you can also obtain permissions for any resource protected by your application. For that, execute the command below:

```bash
export rpt=$(curl -X POST \
 http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
 -H "Authorization: Bearer "$access_token \
 --data "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
 --data "audience=authz-servlet" | jq --raw-output '.access_token' \
 )
```

After executing any of the commands above, you should get a response similar to the following:

```bash
{
    "access_token": "${rpt}",
}
``` 

To finally invoke the resource protected by the application, replace the ``${rpt}`` variable below with the value of the ``access_token`` claim from the response above and execute the following command:

```bash
curl http://localhost:8080/protected/premium \
    -H "Authorization: Bearer ${rpt}"
```

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

* [Spring OAuth 2.0 Resource Server JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
* [Keycloak Authorization Services](https://www.keycloak.org/docs/latest/authorization_services/)
* [Keycloak Documentation](https://www.keycloak.org/documentation)