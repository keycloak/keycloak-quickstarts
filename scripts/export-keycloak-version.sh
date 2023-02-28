#!/bin/bash -e

if [[ ( -n "$GITHUB_BASE_REF" &&  "$GITHUB_BASE_REF" == "latest" ) ]] || [[ ( -n "$QUICKSTART_BRANCH" && "$QUICKSTART_BRANCH" != "main" ) ]]; then
  export KEYCLOAK_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
else
  export KEYCLOAK_VERSION="999.0.0-SNAPSHOT"
fi
