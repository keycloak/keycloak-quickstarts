#!/bin/bash -e

upstream_main() {
  URL="https://github.com/keycloak/keycloak/releases/download/nightly/keycloak-999-SNAPSHOT.tar.gz"
  echo "Downloading Keycloak from: $URL"
  curl -o keycloak-dist.tar.gz "$URL"
  tar xzf keycloak-dist.tar.gz --strip-components=1 -C keycloak-dist
}

latest_release() {
  URL="https://github.com/keycloak/keycloak/releases/download/${VERSION}/keycloak-${VERSION}.tar.gz"
  echo "Downloading Keycloak from: $URL"
  curl -o keycloak-dist.tar.gz "$URL"
  tar xzf keycloak-dist.tar.gz --strip-components=1 -C keycloak-dist
}

mkdir keycloak-dist

if [ -n "$PRODUCT" ] && [ "$PRODUCT" == "true" ]; then
  echo "Using RHSSO distribution: $PRODUCT_VERSION"
  "$PRODUCT_DIST/bin/add-user-keycloak.sh" -u admin -p admin
  exit 0
elif [[ ( -n "$GITHUB_BASE_REF" &&  "$GITHUB_BASE_REF" == "latest" ) ]] || [[ ( -n "$QUICKSTART_BRANCH" && "$QUICKSTART_BRANCH" != "main" ) ]]; then
    VERSION=$(grep -oPm1 "(?<=<version>)[^<]+" pom.xml)
    echo "Using corresponding Keycloak version: $VERSION"
    latest_release
else
  echo "Downloading nightly Keycloak release"
  upstream_main
fi

#keycloak-dist/bin/add-user-keycloak.sh -u admin -p admin
