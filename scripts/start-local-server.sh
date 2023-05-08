#!/bin/bash -e

export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

keycloak-dist/bin/kc.sh start-dev --http-port=8180 --http-relative-path="/auth" --spi-events-listener-sysout-exclude-events=CODE_TO_TOKEN,REFRESH_TOKEN > keycloak.log 2>&1 &

wget --retry-connrefused --waitretry=1 --read-timeout=20 --timeout=15 -t 30 http://localhost:8180/auth