#!/bin/bash -e

NEW_VERSION=$1

mvn versions:set -Dversion.keycloak=$NEW_VERSION -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false -DgroupId=org.keycloak* -DartifactId=*

sed -i 's|\$\$VERSION\$\$|'$NEW_VERSION'|g' kubernetes/keycloak.yaml
sed -i 's|\$\$VERSION\$\$|'$NEW_VERSION'|g' openshift/keycloak.yaml
