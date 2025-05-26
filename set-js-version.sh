#!/bin/bash -e

NEW_VERSION=$1

if [ "$NEW_VERSION" == "" ]; then
  echo "Usage: set-js-version.sh <NEW VERSION>"
  exit 1;
fi

function updateNpmDep() {
  FILE="$1"
  PACKAGE="$2"

  sed -i 's|"'"$PACKAGE"'": "[^"]*"|"'"$PACKAGE"'": "'"$NEW_VERSION"'"|g' $FILE
}


# JS quickstart
updateNpmDep js/spa/package.json "keycloak-js"

echo "Used NPM dependency of keycloak-js: $NEW_VERSION" >&2
