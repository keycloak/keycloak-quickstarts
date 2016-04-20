# service-jaxrs: 

Author: Stan Silvert  
Level: Beginner  
Technologies: Java  
Summary:   
Prerequisites:  
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------

You need to create a client in Keycloak and download the installation file to ``src/main/WEB-APP``. The configuration options when creating the client should be:

* Client ID: You choose
* Access Type: bearer-only

Once you've downloaded the installation file you also need to edit it to enable CORS. Add the following:

    {
        ...,
        "enable-cors": true
    }

