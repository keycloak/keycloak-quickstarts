#!/bin/bash -e

NEW_VERSION=$1

mvn versions:set-property -Dproperty=version.keycloak.client -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
echo "New Keycloak Client Libraries Version: $NEW_VERSION" >&2
