# app-jee: 

Author: Stan Silvert  
Level: Beginner  
Technologies: Java  
Summary:   
Prerequisites:  
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------

The example assumes you have the example services running. If the services are not hosted on ``http://localhost:8080/service`` you need to edit ``web.xml`` and replace the value of ``serviceUrl``.

You need to create a client in Keycloak. The configuration options when creating the client should be:

* Client ID: You choose
* Access Type: confidential
* Root URL: Root URL for where you're hosting the application (for example http://localhost:8080)
* Valid Redirect URIs: /app-jee/*
* Base URL: /app-jee/
* Admin URL: /app-jee/

Then, build the WAR with Maven and install as per the Adapter configuration for your server as described in the Keycloak documentation.
