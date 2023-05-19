nodejs-resource-server: Node.js Resource Server
===================================================

Level: Beginner  
Technologies: Node.js  
Summary: Node.js Service  
Target Product: <span>Keycloak</span>

What is it?
-----------

This quickstart demonstrates how to write a RESTful service with Node.js that is secured with <span>Keycloak</span>.

There are 3 endpoints exposed by the service:

* `public` - requires no authentication
* `secured` - can be invoked by users with the `user` role
* `admin` - can be invoked by users with the `admin` role

The endpoints are very simple and will only return a simple message stating what endpoint was invoked.

System Requirements
-------------------

To compile and run this quickstart you will need:

* Node.js 18.16.0+
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

Alternatively, you can create the realm using the following command:

```shell
npm run create-realm
```

Build and Deploy the Quickstart
-------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to run the quickstart:

   ````
   npm install
   npm start
   ````

Access the Quickstart
---------------------

There are 3 endpoints exposed by the service:

* http://localhost:8080/public - requires no authentication
* http://localhost:8080/secured - can be invoked by users with the `user` role
* http://localhost:8080/admin - can be invoked by users with the `admin` role

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
curl -X POST http://127.0.0.1:8180/realms/quickstart/protocol/openid-connect/token \
-H 'content-type: application/x-www-form-urlencoded' \
-d 'client_id=test-cli' \
-d 'username=alice&password=alice&grant_type=password' | jq --raw-output '.access_token' \
)
```

You can use the same command to obtain tokens on behalf of user `admin`, just make sure to change both `username` and `password` request parameters.

After running the command above, you can now access the `http://127.0.0.1:3000/secured` endpoint
because the user `alice` has the `user` role.

```shell
curl http://localhost:3000/secured \
  -H "Authorization: Bearer "$access_token
```

As a result, you will see the following response from the service:

```json
{"message":"secured"}
```

Running tests
--------------------

Make sure Keycloak is [running](#starting-and-configuring-the-keycloak-server).

1. Open a terminal and navigate to the root directory of this quickstart.

2. Run the following command to build and run tests:

   ````
   npm test
   ````

References
--------------------

* [Keycloak Node.js Adapter](https://www.keycloak.org/docs/latest/securing_apps/#_nodejs_adapter)
* [Keycloak Documentation](https://www.keycloak.org/documentation)