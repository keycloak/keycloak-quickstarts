# Keycloak HA with Traefik TLS Re-encrypt

This quickstart is for **educational purposes only** and should not be used in production.
It demonstrates how to configure Traefik as a TLS re-encrypt load balancer in front of a clustered Keycloak deployment.

## What is TLS re-encrypt?

In TLS re-encrypt mode, the load balancer decrypts the incoming HTTPS connection and establishes a new HTTPS connection to the backend service.
Traefik operates at the HTTP layer (Layer 7) and has a direct access to the HTTP content.

- Traefik can inspect, modify, and cache HTTP headers and the request body. The end-to-end encryption between the client and Keycloak is _not preserved._
- Traefik holds a TLS certificate and a private key used to authenticate itself to the client.
- Traefik holds a TLS certificate and a private key used to authenticate itself to Keycloak.
- Keycloak holds a TLS certificate and a private key used to authenticate itself to Traefik.

## Architecture

![Architecture diagram](architecture.svg)

- **Traefik** listens on port 8443, terminates the incoming HTTPS connections and reencrypts the requests before forwarding them to Keycloak instances.
  It uses the `X-Forwarded-*` HTTP headers to pass the original client IP address to Keycloak.
  It is the only container attached to the `frontend` network, making it the single entry point.
- **Keycloak 1 & 2** are clustered via embedded Infinispan and share the same PostgreSQL database.
  They live exclusively on the `backend` network, which is marked as `internal` and unreachable from the host.
- **PostgreSQL** provides the shared database for Keycloak on the `backend` network.

## Prerequisites

- Docker and Docker Compose
- `openssl` (for certificate generation)

## Quick start

### 1. Generate a TLS certificate

```bash
./generate-certs.sh <hostname>
```

This example uses [nip.io](https://nip.io), a DNS service that maps `127.0.0.1.nip.io` to `127.0.0.1`, avoiding the need to edit `/etc/hosts`:

```bash
./generate-certs.sh 127.0.0.1.nip.io
```

### 2. Start the services

```bash
KC_HOST=<hostname> docker compose up -d
```

For example:

```bash
KC_HOST=127.0.0.1.nip.io docker compose up -d
```

### 3. Access Keycloak

Once the services are up, Keycloak is available at `https://<hostname>:8443`.
Log in to the admin console using credentials `admin` / `admin`.

The browser will show a certificate warning because the certificate is self-signed.
This is expected and can be safely accepted for local testing.

### 4. Check Traefik dashboard

Open [http://127.0.0.1.nip.io:8080/dashboard/](http://127.0.0.1.nip.io:8080/dashboard/) in a browser to verify that both Keycloak backends are healthy.

### 5. Showcase graceful shutdown

This is a walkthrough through a graceful shutdown of one of the Keycloak instances:

1. Open [http://127.0.0.1.nip.io:8080/dashboard/](http://127.0.0.1.nip.io:8080/dashboard/) in a browser to verify that both Keycloak backends are healthy.
2. Send a `TERM` signal to one of the Keycloak containers for a graceful shutdown (takes 30 seconds). Container exits with code 143.
```bash
   docker compose stop keycloak1 -t 60
```
3. Observe that after 5 seconds the `keycloak1` backend is marked as unhealthy in the Traefik dashboard.
   Requests are still served by the node until it shuts down gracefully after 30 seconds.
4. Start the Keycloak container again:
```bash
   docker compose start keycloak1
```
5. Observe that after 5 seconds the `keycloak1` backend is marked as healthy again in the Traefik dashboard.

### 6. Stop the services

```bash
docker compose down
```

## Traefik configuration

The key parts of `keycloak.yaml` are explained below. The `keycloak.yaml` file contains the
dynamic configuration — routing rules, TLS certificates and backend transport settings.

**Certificate for external access:**

```yaml
tls:
  certificates:
    - certFile: /certs/traefik-external/cert.pem
      keyFile: /certs/traefik-external/key.pem
```

Traefik will use this certificate to authenticate itself to the browser.
This is the certificate the client sees when connecting to Traefik on port 8443.
It is separate from the internal certificate used for mTLS between Traefik and Keycloak.

**HTTP health check on the management port:**

```yaml
healthCheck:
  path: /health/ready
  port: 9000
  scheme: http
  interval: "5s"
  timeout: "3s"
```

Traefik performs health checks against Keycloak's management endpoint `/health/ready`, expecting an HTTP 200 response.
This endpoint is only available when Keycloak is configured with `KC_HEALTH_ENABLED=true` and `KC_METRICS_ENABLED=true`.

Note: The management port uses HTTP (`KC_HTTP_MANAGEMENT_SCHEME: http`) so that Traefik can
perform health checks without needing to present a client certificate on port 9000.

**mTLS to Keycloak (serversTransport):**

```yaml
serversTransports:
  keycloak-transport:
    certificates:
      - certFile: /certs/traefik-internal/cert.pem
        keyFile: /certs/traefik-internal/key.pem
    rootCAs:
      - /certs/keycloak1-cert.pem
      - /certs/keycloak2-cert.pem
```

- `certificates` configures the certificate Traefik presents to Keycloak during the mTLS handshake.
- `rootCAs` configures the certificates used to verify Keycloak's identity.

**Graceful shutdown timing:**

Health checks poll every 5 seconds. It may take up to 5 seconds for Traefik to detect that a Keycloak instance is down.
For this reason, Keycloak is configured with `KC_SHUTDOWN_DELAY=30s` and
`KC_SHUTDOWN_TIMEOUT=30s`, giving Traefik enough time to detect the shutdown and allowing existing client connections to drain gracefully.

Note: Although a longer shutdown delay is typically associated with TLS passthrough setups,
it is also recommended for TLS re-encrypt to ensure in-flight requests complete before the
Keycloak instance stops accepting new connections.

## Keycloak configuration

**Configure accepted proxy headers:**

```
KC_PROXY_HEADERS: xforwarded
```
Keycloak will accept the `X-Forwarded-*` HTTP headers that Traefik adds automatically.

**Configure the certificate and private key for HTTPS:**

```
KC_HTTPS_CERTIFICATE_FILE: /opt/keycloak/certs/cert.pem
KC_HTTPS_CERTIFICATE_KEY_FILE: /opt/keycloak/certs/key.pem  
```
The certificate is used by Traefik to verify Keycloak's identity during the TLS handshake.

**Configure mTLS:**

```
KC_HTTPS_CLIENT_AUTH: required
KC_HTTPS_TRUST_STORE_FILE: /opt/keycloak/conf/https-truststore/traefik-internal-cert.pem
```

Mutual TLS (mTLS) is enabled by requiring Keycloak to authenticate the connecting client.
`KC_HTTPS_CLIENT_AUTH: required` enforces that any client connecting to Keycloak on port 8443
must present a valid certificate. `KC_HTTPS_TRUST_STORE_FILE` specifies the truststore
containing the only accepted certificate — Traefik's internal certificate — ensuring that
only Traefik can establish a connection to Keycloak on the backend network.

```
KC_HTTPS_MANAGEMENT_CLIENT_AUTH: none
```
This setting disables the mTLS requirement for the management endpoint (port 9000).
While the main HTTPS port (8443) requires Traefik to present its `traefik-internal` certificate,
the management port is used exclusively for health checks and does not need mutual authentication.
This allows Traefik to perform health checks against `/health/ready` without needing to present
a client certificate on every poll.

## Resources

- [Traefik Documentation](https://doc.traefik.io/traefik/)
- [Traefik ServersTransport](https://doc.traefik.io/traefik/routing/services/#serverstransport)
- [Traefik TLS Configuration](https://doc.traefik.io/traefik/https/tls/)
- [Keycloak Reverse Proxy Configuration](https://www.keycloak.org/server/reverseproxy)
- [Configuring trusted certificates for mTLS](https://www.keycloak.org/server/mutual-tls)