#!/bin/bash -e

export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

if [ "$1" = "extension" ]; then
  echo "Adding default providers when starting Keycloak server";

  # extensions/event-listener-sysout example
  SERVER_ARGS="--spi-events-listener-sysout-exclude-events=CODE_TO_TOKEN,REFRESH_TOKEN";

  # extensions/action-token-authenticator example
  SERVER_ARGS="$SERVER_ARGS --spi-action-token-handler-external-app-notification-hmac-secret=aSqzP4reFgWR4j94BDT1r+81QYp/NYbY9SBwXtqV1ko=";

  # extensions/action-token-required-action example
  SERVER_ARGS="$SERVER_ARGS --spi-action-token-handler-external-app-reqaction-notification-hmac-secret=aSqzP4reFgWR4j94BDT1r+81QYp/NYbY9SBwXtqV1ko= --spi-required-action-redirect-to-external-application-external-application-url=http://127.0.0.1:8080/action-token-responder-example/external-action.jsp?token={TOKEN}";

  # extensions/event-store-mem example
  SERVER_ARGS="$SERVER_ARGS --spi-events-store-provider=in-mem";
fi

keycloak-dist/bin/kc.sh start-dev --http-port=8180 --http-relative-path="/auth" $SERVER_ARGS > keycloak.log 2>&1 &

wget --retry-connrefused --waitretry=3 --read-timeout=20 --timeout=15 -t 30 http://localhost:8180/auth