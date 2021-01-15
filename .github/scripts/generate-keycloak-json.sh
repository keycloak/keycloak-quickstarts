#!/bin/bash -x

## This is a helper script for generating "keycloak.json" files from "keycloak-example.json".
## Use this to test your changes against the Enforcer Plugin.

for f in $(find . -type f -name 'keycloak-example.json'); do
   cp "$f" "${f%-example.json}.json"
done

for f in $(find . -type f -name 'keycloak-saml-example.xml'); do
   cp "$f" "${f%-example.xml}.xml"
done
