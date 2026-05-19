#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 4 ]; then
    echo "Usage: $0 <hostname> <client-key> <client-cert> <subject-dn>"
    echo ""
    echo "Arguments:"
    echo "  hostname     Hostname of the Keycloak cluster"
    echo "  client-key   Path to the client private key (PEM)"
    echo "  client-cert  Path to the client certificate (PEM)"
    echo "  subject-dn   Expected subject DN for the X.509 client authenticator"
    echo ""
    echo "Example:"
    echo "  $0 127.0.0.1.nip.io ./certs/client/key.pem ./certs/client/cert.pem 'CN=test-client'"
    exit 1
fi

HOSTNAME="$1"
CLIENT_KEY="$2"
CLIENT_CERT="$3"
SUBJECT_DN="$4"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CACERT="${SCRIPT_DIR}/certs/haproxy-external/cert.pem"
BASE_URL="https://${HOSTNAME}:8443"
MTLS_CLIENT_ID="mtls-client"

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

echo ""
echo "Obtaining admin token..."
ADMIN_RESPONSE=$(curl --silent --cacert "$CACERT" -XPOST \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d "grant_type=password&client_id=admin-cli&username=admin&password=admin" \
    "${BASE_URL}/realms/master/protocol/openid-connect/token")

ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.access_token')
if [ "$ADMIN_TOKEN" = "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo "Failed to obtain admin access token."
    echo "$ADMIN_RESPONSE" | jq .
    exit 1
fi
echo "Admin token obtained."

CLIENTS_URL="${BASE_URL}/admin/realms/master/clients"

echo ""
echo "Checking if client '${MTLS_CLIENT_ID}' already exists..."
EXISTING_CLIENT=$(curl --silent --cacert "$CACERT" \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    "${CLIENTS_URL}?clientId=${MTLS_CLIENT_ID}")

EXISTING_ID=$(echo "$EXISTING_CLIENT" | jq -r '.[0].id // empty')

if [ -n "$EXISTING_ID" ]; then
    echo "Client exists (id=${EXISTING_ID}), deleting..."
    curl --silent --cacert "$CACERT" -XDELETE \
        -H "Authorization: Bearer ${ADMIN_TOKEN}" \
        "${CLIENTS_URL}/${EXISTING_ID}"
    echo "Deleted."
fi

echo ""
echo "Creating client '${MTLS_CLIENT_ID}' with X.509 authenticator (subject DN: ${SUBJECT_DN})..."
HTTP_STATUS=$(curl --silent --cacert "$CACERT" -XPOST \
    -o /dev/null --write-out '%{http_code}' \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    -H 'Content-Type: application/json' \
    -d "$(cat <<EOF
{
    "clientId": "${MTLS_CLIENT_ID}",
    "enabled": true,
    "clientAuthenticatorType": "client-x509",
    "serviceAccountsEnabled": true,
    "attributes": {
        "x509.subjectdn": "${SUBJECT_DN}",
        "x509.allow.regex.pattern.comparison": "false"
    }
}
EOF
)" \
    "${CLIENTS_URL}")

if [ "$HTTP_STATUS" != "201" ]; then
    echo "Failed to create client (HTTP ${HTTP_STATUS})."
    exit 1
fi
echo "Client '${MTLS_CLIENT_ID}' created."

echo ""
echo "Requesting access token with client certificate..."
TOKEN_RESPONSE=$(curl --silent --cacert "$CACERT" \
    --cert "$CLIENT_CERT" --key "$CLIENT_KEY" \
    -XPOST \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d "grant_type=client_credentials&client_id=${MTLS_CLIENT_ID}" \
    "${BASE_URL}/realms/master/protocol/openid-connect/token")

echo "$TOKEN_RESPONSE" | jq .

ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token')
if [ "$ACCESS_TOKEN" = "null" ] || [ -z "$ACCESS_TOKEN" ]; then
    echo ""
    echo "FAILED: No access token granted."
    exit 1
fi

echo ""
echo "SUCCESS: Access token granted for client '${MTLS_CLIENT_ID}' via X.509 client certificate."
