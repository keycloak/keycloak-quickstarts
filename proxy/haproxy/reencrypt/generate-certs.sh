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

mkdir -p "$CERTS_DIR/haproxy"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=${HOSTNAME}" \
    -addext "subjectAltName=DNS:${HOSTNAME}" \
    -keyout "$CERTS_DIR/haproxy/key.pem" \
    -out "$CERTS_DIR/haproxy/cert.pem"

cat "$CERTS_DIR/haproxy/cert.pem" "$CERTS_DIR/haproxy/key.pem" > "$CERTS_DIR/haproxy/combined.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/haproxy/key.pem" "$CERTS_DIR/haproxy/cert.pem"

mkdir -p "$CERTS_DIR/haproxy-internal"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=haproxy" \
    -addext "subjectAltName=DNS:haproxy" \
    -keyout "$CERTS_DIR/haproxy-internal/key.pem" \
    -out "$CERTS_DIR/haproxy-internal/cert.pem"

cat "$CERTS_DIR/haproxy-internal/cert.pem" "$CERTS_DIR/haproxy-internal/key.pem" > "$CERTS_DIR/haproxy-internal/combined.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/haproxy-internal/key.pem" "$CERTS_DIR/haproxy-internal/cert.pem"

echo "Certificates generated in $CERTS_DIR"
