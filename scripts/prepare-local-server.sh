#!/bin/bash -e

mkdir keycloak-dist

if [[ ( -n "$GITHUB_BASE_REF" &&  "$GITHUB_BASE_REF" == "latest" ) ]] || [[ ( -n "$QUICKSTART_BRANCH" && "$QUICKSTART_BRANCH" != "main" ) ]]; then
  VERSION=$(grep -oPm1 "(?<=<version>)[^<]+" pom.xml)
  echo "Using corresponding Keycloak version: $VERSION"
  URL="https://github.com/keycloak/keycloak/releases/download/${VERSION}/keycloak-${VERSION}.tar.gz"
else
  echo "Downloading nightly Keycloak release"
  URL="https://github.com/keycloak/keycloak/releases/download/nightly/keycloak-999.0.0-SNAPSHOT.tar.gz"
fi

wget -q -O keycloak-dist.tar.gz "$URL"
tar xzf keycloak-dist.tar.gz --strip-components=1 -C keycloak-dist
