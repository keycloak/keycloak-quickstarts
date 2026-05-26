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

mkdir -p "$CERTS_DIR/client-ca"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=Client CA" \
    -addext "basicConstraints=critical,CA:TRUE" \
    -addext "keyUsage=critical,keyCertSign,cRLSign" \
    -keyout "$CERTS_DIR/client-ca/key.pem" \
    -out "$CERTS_DIR/client-ca/cert.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/client-ca/key.pem" "$CERTS_DIR/client-ca/cert.pem"

mkdir -p "$CERTS_DIR/client-intermediate-ca"

openssl req \
    -newkey rsa:2048 \
    -nodes \
    -subj "/CN=Client Intermediate CA" \
    -keyout "$CERTS_DIR/client-intermediate-ca/key.pem" \
    -out "$CERTS_DIR/client-intermediate-ca/csr.pem"

openssl x509 -req \
    -in "$CERTS_DIR/client-intermediate-ca/csr.pem" \
    -CA "$CERTS_DIR/client-ca/cert.pem" \
    -CAkey "$CERTS_DIR/client-ca/key.pem" \
    -CAcreateserial \
    -days 365 \
    -extfile <(printf "basicConstraints=critical,CA:TRUE,pathlen:0\nkeyUsage=critical,keyCertSign,cRLSign") \
    -out "$CERTS_DIR/client-intermediate-ca/cert.pem"

rm -f "$CERTS_DIR/client-intermediate-ca/csr.pem" "$CERTS_DIR/client-ca/cert.srl"

cat "$CERTS_DIR/client-intermediate-ca/cert.pem" "$CERTS_DIR/client-ca/cert.pem" > "$CERTS_DIR/client-ca/chain.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/client-intermediate-ca/key.pem" "$CERTS_DIR/client-intermediate-ca/cert.pem"

mkdir -p "$CERTS_DIR/client"

openssl req \
    -newkey rsa:2048 \
    -nodes \
    -subj "/CN=test-client" \
    -keyout "$CERTS_DIR/client/key.pem" \
    -out "$CERTS_DIR/client/csr.pem"

openssl x509 -req \
    -in "$CERTS_DIR/client/csr.pem" \
    -CA "$CERTS_DIR/client-intermediate-ca/cert.pem" \
    -CAkey "$CERTS_DIR/client-intermediate-ca/key.pem" \
    -CAcreateserial \
    -days 365 \
    -out "$CERTS_DIR/client/cert.pem"

rm -f "$CERTS_DIR/client/csr.pem" "$CERTS_DIR/client-intermediate-ca/cert.srl"

cat "$CERTS_DIR/client/cert.pem" "$CERTS_DIR/client-intermediate-ca/cert.pem" > "$CERTS_DIR/client/fullchain.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/client/key.pem" "$CERTS_DIR/client/cert.pem"

echo "Certificates generated in $CERTS_DIR"
