#!/bin/bash -e

export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

SERVER_ARGS=""
if [ "$1" = "add-providers-config" ]; then
  echo "Adding providers configuration when starting Keycloak server";
  SERVER_ARGS="--spi-events-listener-sysout-exclude-events=CODE_TO_TOKEN,REFRESH_TOKEN --spi-events-store-provider=in-mem > keycloak.log"
fi

keycloak-dist/bin/kc.sh start-dev --http-port=8180 --http-relative-path="/auth" $SERVER_ARGS > keycloak.log 2>&1 &

wget --retry-connrefused --waitretry=3 --read-timeout=20 --timeout=15 -t 30 http://localhost:8180/auth