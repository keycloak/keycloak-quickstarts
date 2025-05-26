#!/bin/bash -e

NEW_VERSION=$1

if [ "$NEW_VERSION" == "" ]; then
  echo "Usage: set-server-version.sh <NEW VERSION>"
  exit 1;
fi

function updateNpmDep() {
  FILE="$1"
  PACKAGE="$2"

  sed -i 's|"'"$PACKAGE"'": "[^"]*"|"'"$PACKAGE"'": "'"$NEW_VERSION"'"|g' $FILE
}

mvn versions:set-property -Dproperty=version.keycloak -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false

sed -i 's/ image:\(.*keycloak\):.*/ image:\1:'"$NEW_VERSION"'/g' kubernetes/keycloak.yaml
sed -i 's/ version: .*/ version: '"$NEW_VERSION"'/g' openshift/keycloak.yaml
sed -i 's/ image:\(.*keycloak\):.*/ image:\1:'"$NEW_VERSION"'/g' openshift/keycloak.yaml

if [[ $NEW_VERSION == "999.0.0-SNAPSHOT" ]]; then
  # Use the nightly versions of adapters
  NPM_ADMIN_CLIENT="https://github.com/keycloak/keycloak/releases/download/nightly/keycloak-admin-client-999.0.0-SNAPSHOT.tgz"
  NPM_NODE_ADAPTER="https://github.com/keycloak/keycloak-nodejs-connect/releases/download/nightly/keycloak-nodejs-connect.tgz";
else
  NPM_ADMIN_CLIENT=$NEW_VERSION
  NPM_NODE_ADAPTER=$NEW_VERSION
fi

# JS quickstart
updateNpmDep js/spa/package.json "@keycloak/keycloak-admin-client"

# NodeJS quickstart
updateNpmDep nodejs/resource-server/package.json "@keycloak/keycloak-admin-client"

echo "New Mvn Version: $NEW_VERSION" >&2
echo "Used NPM dependency of keycloak-admin-client: $NPM_ADMIN_CLIENT" >&2
echo "Used NPM dependency of node-adapter: $NPM_NODE_ADAPTER" >&2
