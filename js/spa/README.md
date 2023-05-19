js-spa: Node.js Single Page Application
===================================================

Level: Beginner  
Technologies: JavaScript, HTML5, Node.js  
Summary: Single Page Application protected using the Keycloak JavaScript Adapter  
Target Product: <span>Keycloak</span>

What is it?
-----------

This quickstart demonstrates how to write a Single Page Application(SPA) that authenticates
using Keycloak. Once authenticated the application shows how to invoke a service secured with Keycloak.

The static resources are served by Node.js from the [public](public) directory. The same resources can also be deployed
on the web server of your preference.

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

You can access the application with the following URL: <http://localhost:8080>.

Try to authenticate with any of these users:

| Username | Password | Roles              |
|----------|----------|--------------------|
| alice    | alice    | user               |
| admin    | admin    | admin              |

Once authenticated, you are redirected to the application and you can perform the following actions:

* Show the Access Token
* Show the ID Token
* Refresh Token
* Logout

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

* [Keycloak JavaScript Adapter](https://www.keycloak.org/docs/latest/securing_apps/#_javascript_adapter)
* [Keycloak Documentation](https://www.keycloak.org/documentation)
