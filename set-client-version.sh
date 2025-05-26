#!/bin/bash -e

NEW_VERSION=$1

if [ "$NEW_VERSION" == "" ]; then
  echo "Usage: set-client-version.sh <NEW VERSION>"
  exit 1;
fi

mvn versions:set-property -Dproperty=version.keycloak.client -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
echo "New Keycloak Client Libraries Version: $NEW_VERSION" >&2
