#!/bin/bash
if [ ! -d "keycloak-dist" ]; then
  exit 0
fi

if [ -n "$GITHUB_BASE_REF" ] && [[ "$GITHUB_BASE_REF" == "latest" ]]; then
  export KEYCLOAK_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
else
  export KEYCLOAK_VERSION=$(mvn -f keycloak/pom.xml -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi
