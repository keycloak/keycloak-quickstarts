# app-html5: 

Author: Stan Silvert  
Level: Beginner  
Technologies: Java  
Summary:   
Prerequisites:  
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------

The example assumes you have the example services running. If the services are not hosted on ``http://localhost:8080/service`` you need to edit ``app.js`` and replace the value of ``serviceUrl``.

You need to create a client in Keycloak and download the installation file. The configuration options when creating the client should be:

* Client ID: You choose
* Access Type: public
* Root URL: Root URL for where you're hosting the application (for example http://localhost)
* Valid Redirect URIs: ``/*``
* Web Origins: ``+``
