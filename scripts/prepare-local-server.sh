#!/bin/bash -e

upstream_main() {
  git clone https://github.com/keycloak/keycloak
  mvn clean install -Pdistribution -DskipTests -f keycloak -B
  find keycloak/distribution/server-dist/target -maxdepth 1 -type f -name 'keycloak-[[:digit:]]*.tar.gz' -exec tar xzf {} --strip-components=1 -C keycloak-dist \;
}

latest_release() {
  URL="https://repo1.maven.org/maven2/org/keycloak/keycloak-server-dist/${VERSION}/keycloak-server-dist-${VERSION}.tar.gz"
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
  echo "Building Keycloak from upstream/main"
  upstream_main
fi

keycloak-dist/bin/add-user-keycloak.sh -u admin -p admin
