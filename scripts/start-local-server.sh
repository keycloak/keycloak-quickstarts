#!/bin/bash -e

export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

SERVER_ARGS="--spi-events-listener-sysout-exclude-events=CODE_TO_TOKEN,REFRESH_TOKEN";
SERVER_ARGS="$SERVER_ARGS --spi-action-token-handler-external-app-notification-hmac-secret=aSqzP4reFgWR4j94BDT1r+81QYp/NYbY9SBwXtqV1ko=";
if [ "$1" = "add-providers-config" ]; then
  echo "Adding default providers when starting Keycloak server";
  SERVER_ARGS="$SERVER_ARGS --spi-events-store-provider=in-mem";
fi

keycloak-dist/bin/kc.sh start-dev --http-port=8180 --http-relative-path="/auth" $SERVER_ARGS > keycloak.log 2>&1 &

wget --retry-connrefused --waitretry=3 --read-timeout=20 --timeout=15 -t 30 http://localhost:8180/auth