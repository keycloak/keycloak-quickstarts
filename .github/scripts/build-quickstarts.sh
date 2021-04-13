#!/bin/bash -e

cp -f maven-settings.xml ~/.m2/settings.xml

# generate keycloak.json
for f in $(find . -type f -name 'keycloak-example.json'); do
   cp "$f" "${f%-example.json}.json"
done

for f in $(find . -type f -name 'keycloak-saml-example.xml'); do
   cp "$f" "${f%-example.xml}.xml"
done

mvn clean install -DskipTests -B
cp authz-js-policies/target/authz-js-policies.jar keycloak-dist/standalone/deployments