# app-html5: 

Author: Stan Silvert  
Level: Beginner  
Technologies: JavaScript, HTML5  
Summary: Simple HTML5 application exposing three different REST endpoints.   
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------

Simple HTML5 application exposing three different REST endpoints.

Settings
--------

The example assumes you have the example services running. If the services are not hosted on ``http://localhost:8080/service`` you need to edit ``app.js`` and replace the value of ``serviceUrl``.

You need to create a client in RH-SSO and download the installation file. The configuration options when creating the client should be:

* Client ID: You choose
* Access Type: public
* Root URL: Root URL for where you're hosting the application (for example http://localhost)
* Valid Redirect URIs: ``/*``
* Web Origins: ``+``
