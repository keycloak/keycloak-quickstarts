#!/bin/bash -e

mkdir keycloak-dist

if [ "$NIGHTLY_TEST" == "true" ]; then
  # the nighly version is used
  echo "Downloading Keycloak release nightly"
  URL="https://github.com/keycloak/keycloak/releases/download/nightly/keycloak-999.0.0-SNAPSHOT.tar.gz"
else
  # normal execution with current keycloak versions
  VERSION=$(grep -oPm1 "(?<=<version.keycloak>)[^<]+" pom.xml)
  echo "Downloading Keycloak release $VERSION"
  URL="https://github.com/keycloak/keycloak/releases/download/${VERSION}/keycloak-${VERSION}.tar.gz"
fi

wget -q -O keycloak-dist.tar.gz "$URL"
tar xzf keycloak-dist.tar.gz --strip-components=1 -C keycloak-dist
