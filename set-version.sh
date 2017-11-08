#!/bin/bash -e

NEW_VERSION=$1

mvn versions:update-parent -Dversion.keycloak=$NEW_VERSION -DparentVersion=$NEW_VERSION -DgenerateBackupPoms=false -Pbump-version
mvn versions:set -Dversion.keycloak=$NEW_VERSION -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false -DgroupId=org.keycloak* -DartifactId=* -Pbump-version
