# app-profile-html5: 

Author: Stan Silvert  
Level: Beginner  
Technologies: JavaScript, HTML5  
Summary: Simple HTML5 application allowing login/logout and displaying full name of the user obtained from the token.  
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------
Simple HTML5 application allowing login/logout and displaying full name of the user obtained from the token.

Settings
--------

You need to create a client in Keycloak and download the installation file. The configuration options when creating the client should be:

* Client ID: You choose
* Access Type: public
* Root URL: Root URL for where you're hosting the application (for example http://localhost)
* Valid Redirect URIs: ``/*``
* Web Origins: ``+``
