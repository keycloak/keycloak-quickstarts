#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 1 ]; then
    echo "Usage: $0 <hostname>"
    echo "Example: $0 127.0.0.1.nip.io"
    exit 1
fi

HOSTNAME="$1"
CERTS_DIR="$(cd "$(dirname "$0")" && pwd)/certs"

mkdir -p "$CERTS_DIR/keycloak1"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=keycloak1" \
    -addext "subjectAltName=DNS:keycloak1" \
    -keyout "$CERTS_DIR/keycloak1/key.pem" \
    -out "$CERTS_DIR/keycloak1/cert.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/keycloak1/key.pem" "$CERTS_DIR/keycloak1/cert.pem"

mkdir -p "$CERTS_DIR/keycloak2"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=keycloak2" \
    -addext "subjectAltName=DNS:keycloak2" \
    -keyout "$CERTS_DIR/keycloak2/key.pem" \
    -out "$CERTS_DIR/keycloak2/cert.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/keycloak2/key.pem" "$CERTS_DIR/keycloak2/cert.pem"

mkdir -p "$CERTS_DIR/traefik-external"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=${HOSTNAME}" \
    -addext "subjectAltName=DNS:${HOSTNAME}" \
    -keyout "$CERTS_DIR/traefik-external/key.pem" \
    -out "$CERTS_DIR/traefik-external/cert.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/traefik-external/key.pem" "$CERTS_DIR/traefik-external/cert.pem"

mkdir -p "$CERTS_DIR/traefik-internal"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=traefik" \
    -addext "subjectAltName=DNS:traefik" \
    -keyout "$CERTS_DIR/traefik-internal/key.pem" \
    -out "$CERTS_DIR/traefik-internal/cert.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/traefik-internal/key.pem" "$CERTS_DIR/traefik-internal/cert.pem"

echo "Certificates generated in $CERTS_DIR"
