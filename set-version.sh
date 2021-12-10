#!/bin/bash -e

NEW_VERSION=$1

LATEST_KEYCLOAK_VERSION=`curl -s https://api.github.com/repos/keycloak/keycloak/releases/latest | jq -r .tag_name`

mvn versions:update-parent -Dversion.keycloak=$LATEST_KEYCLOAK_VERSION -DparentVersion=$NEW_VERSION -DgenerateBackupPoms=false -Pbump-version
mvn versions:set -Dversion.keycloak=$LATEST_KEYCLOAK_VERSION -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false -DgroupId=org.keycloak* -DartifactId=* -Pbump-version

sed -i "s|keycloak/keycloak:.*|keycloak/keycloak:$NEW_VERSION|" kubernetes-examples/keycloak.yaml
sed -i "s|keycloak/keycloak:.*|keycloak/keycloak:$NEW_VERSION|" openshift-examples/keycloak.yaml
