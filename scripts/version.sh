#!/bin/bash

export VERSION=$(curl -s https://repo1.maven.org/maven2/org/keycloak/keycloak-server-dist/ | sed -e 's/<[^>]*>//g' | grep -e '[0-9]\{1,2\}\.[0-9]\.[0-9]/' | cut -d '/' -f1 | tail -n1)
export KEYCLOAK="keycloak-${VERSION}"