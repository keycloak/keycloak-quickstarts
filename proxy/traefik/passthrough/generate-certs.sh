#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 1 ]; then
    echo "Usage: $0 <hostname>"
    echo "Example: $0 127.0.0.1.nip.io"
    exit 1
fi

HOSTNAME="$1"
CERTS_DIR="$(cd "$(dirname "$0")" && pwd)/certs"

mkdir -p "$CERTS_DIR"

openssl req -x509 \
    -newkey rsa:2048 \
    -nodes \
    -days 365 \
    -subj "/CN=${HOSTNAME}" \
    -addext "subjectAltName=DNS:${HOSTNAME}" \
    -keyout "$CERTS_DIR/key.pem" \
    -out "$CERTS_DIR/cert.pem"

# Not safe! Everybody in the PC can read the private key data.
chmod 644 "$CERTS_DIR/key.pem" "$CERTS_DIR/cert.pem"

echo "Certificates generated in $CERTS_DIR"
