#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 2 ]; then
    echo "Usage: $0 <hostname> <cacert>"
    echo "Example: $0 pedro-desktop.local ./certs/cert.pem"
    exit 1
fi

HOSTNAME="$1"
CACERT="$2"
BASE_URL="https://${HOSTNAME}:8443"

# Poll until Keycloak admin console is ready (timeout after 60s)
echo "Waiting for Keycloak at ${BASE_URL}/admin/master/console/ ..."
ELAPSED=0
until curl --silent --cacert "$CACERT" --output /dev/null --write-out '%{http_code}' \
    "${BASE_URL}/admin/master/console/" | grep -q '^200$'; do
    if [ "$ELAPSED" -ge 60 ]; then
        echo "Timed out waiting for Keycloak after 60s."
        exit 1
    fi
    echo "  not ready, retrying in 5s..."
    sleep 5
    ELAPSED=$((ELAPSED + 5))
done
echo "Keycloak is ready."

# Client login
echo ""
echo "Logging in with admin-cli..."
LOGIN_RESPONSE=$(curl --silent --cacert "$CACERT" -XPOST \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d "grant_type=password&client_id=admin-cli&username=admin&password=admin&scope=openid profile email" \
    "${BASE_URL}/realms/master/protocol/openid-connect/token")

echo "$LOGIN_RESPONSE" | jq .

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.access_token')
if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
    echo "Failed to obtain access token."
    exit 1
fi

# Client session stats
echo ""
echo "Fetching client session stats..."
curl --silent --cacert "$CACERT" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H 'Content-Type: application/json' \
    "${BASE_URL}/admin/realms/master/client-session-stats" | jq .

# Refresh token
REFRESH_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.refresh_token')
echo ""
echo "Refreshing token..."
REFRESH_RESPONSE=$(curl --silent --cacert "$CACERT" -XPOST \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d "grant_type=refresh_token&client_id=admin-cli&refresh_token=${REFRESH_TOKEN}" \
    "${BASE_URL}/realms/master/protocol/openid-connect/token")

echo "$REFRESH_RESPONSE" | jq .

TOKEN=$(echo "$REFRESH_RESPONSE" | jq -r '.access_token')
if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
    echo "Failed to refresh access token."
    exit 1
fi

# List all sessions
echo ""
echo "Listing all sessions..."
curl --silent --cacert "$CACERT" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H 'Content-Type: application/json' \
    "${BASE_URL}/admin/realms/master/ui-ext/sessions?first=0&max=11&type=ALL&search=" | jq .

# Revoke token
TOKEN=$(echo "$REFRESH_RESPONSE" | jq -r '.refresh_token')
echo ""
echo "Revoking refresh token..."
LOGOUT_RESPONSE=$(curl --silent --cacert "$CACERT" -XPOST \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d "client_id=admin-cli&token=${TOKEN}" \
    "${BASE_URL}/realms/master/protocol/openid-connect/revoke")

echo "$LOGOUT_RESPONSE" | jq .

echo ""
echo "Done."
