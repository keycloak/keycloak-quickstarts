#!/bin/bash -e

if [ -n "$PRODUCT" ] && [ "$PRODUCT" == "true" ]; then
  args="-s $PRODUCT_MVN_SETTINGS  -Dmaven.repo.local=$PRODUCT_MVN_REPO"
else
  args="-s maven-settings.xml"
fi

# generate keycloak.json
for f in $(find . -type f -name 'keycloak-example.json'); do
   cp "$f" "${f%-example.json}.json"
done

for f in $(find . -type f -name 'keycloak-saml-example.xml'); do
   cp "$f" "${f%-example.xml}.xml"
done

mvn clean install $args -DskipTests -B
if [ -n "$PRODUCT" ] && [ "$PRODUCT" == "true" ]; then
  dist=$PRODUCT_DIST
else
  dist="keycloak-dist"
fi

cp authz-js-policies/target/authz-js-policies.jar $dist/standalone/deployments
