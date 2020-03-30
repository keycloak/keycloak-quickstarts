#!/bin/bash -e

NEW_VERSION=$1

LATEST_KEYCLOAK_VERSION=`curl -s "http://search.maven.org/solrsearch/select?q=g:org.keycloak%20AND%20a:keycloak-parent&rows=1&wt=json" | jq -r .response.docs[0].latestVersion`

mvn versions:update-parent -Dversion.keycloak=$LATEST_KEYCLOAK_VERSION -DparentVersion=$NEW_VERSION -DgenerateBackupPoms=false -Pbump-version
mvn versions:set -Dversion.keycloak=$LATEST_KEYCLOAK_VERSION -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false -DgroupId=org.keycloak* -DartifactId=* -Pbump-version

sed -i "s|keycloak/keycloak:.*|keycloak/keycloak:$NEW_VERSION|" kubernetes-examples/keycloak.yaml
sed -i "s|keycloak/keycloak:.*|keycloak/keycloak:$NEW_VERSION|" openshift-examples/keycloak.yaml
