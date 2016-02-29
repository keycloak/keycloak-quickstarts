You need to create a client in Keycloak. The configuration options when creating the client should be:

Settings
-----------
* Client ID: app-profile-jee-saml
* Enabled: ON
* Consent Required: OFF
* direct-grants-only: OFF
* Client Protocol: saml
* Include AuthnStatement: ON
* Sign Documents: OFF
* Sign Assertions: OFF
* Encrypt Assertions: OFF
* Client Signature Required: OFF
* Force POST Binding: OFF
* Front Channel Logout: OFF
* Force Name ID Format: OFF
* Name ID Format: username
* Root URL: <blank>
* Valid Redirect URIs: http://localhost:8080/app-profile-jee-saml/*
* Base URL: http://localhost:8080/app-profile-jee-saml/
* Master SAML Processing URL: http://localhost:8080/app-profile-jee-saml/saml

Mappers
------------
Add all builtin mappers

Then, build the WAR with Maven and install as per the Adapter configuration for your server as described in the Keycloak documentation.