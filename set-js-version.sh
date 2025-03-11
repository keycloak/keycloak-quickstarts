#!/bin/bash -e

NEW_VERSION=$1

function updateNpmDep() {
  FILE="$1"
  PACKAGE="$2"

  sed -i 's|"'"$PACKAGE"'": "[^"]*"|"'"$PACKAGE"'": "'"$NEW_VERSION"'"|g' $FILE
}


# JS quickstart
updateNpmDep js/spa/package.json "keycloak-js"

echo "Used NPM dependency of keycloak-js: $NEW_VERSION" >&2
