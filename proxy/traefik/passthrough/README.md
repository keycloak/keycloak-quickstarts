# Keycloak HA with Traefik TLS Passthrough

This quickstart is for **educational purposes only** and should not be used in production.
It demonstrates how to configure Traefik as a TLS passthrough load balancer in front of a clustered Keycloak deployment.

## What is TLS passthrough?

In TLS passthrough mode, the load balancer forwards encrypted TLS traffic directly to the backend servers without decrypting it.
Traefik operates at the TCP layer (Layer 4) and has no visibility into the HTTP content.
The TLS connection is terminated by Keycloak itself, which means:

- Keycloak holds the TLS certificate and private key, not the proxy.
- Traefik cannot inspect, modify, or cache HTTP headers or the request body.
- End-to-end encryption is preserved between the client and Keycloak.

## Architecture

![Architecture diagram](traefikArchitechture.png)

- **Traefik** listens on port 8443 and forwards raw TCP traffic to both Keycloak instances using round-robin.
  It uses PROXY protocol v2 to pass the original client IP address to Keycloak.
  It is attached to both the `frontend` and `backend` networks, making it the single entry point.
- **Keycloak 1 & 2** are clustered via embedded Infinispan.
  They terminate TLS and share the same PostgreSQL database.
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

This example uses [nip.io](https://nip.io), a DNS service that maps `127.0.0.1.nip.io` to `127.0.0.1`, avoiding the need
to edit `/etc/hosts`:

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

Open http://127.0.0.1:8080/dashboard/ in a browser to verify that Traefik is running and both Keycloak backends are registered as TCP services

### 5. Showcase graceful shutdown

This is a walkthrough through a graceful shutdown of one of the Keycloak instances:

1. Open [http://127.0.0.1:8080/dashboard/](http://127.0.0.1:8080/dashboard/) in a browser to verify that Traefik is running and both Keycloak backends are healthy.

2. Send a `TERM` signal to one of the Keycloak containers for a graceful shutdown (takes 30 seconds). Container exits with code 143.
   ```bash
   docker stop passthrough-keycloak1-1 -t 60
   ```
3. Observe that Traefik detects the backend is unavailable and stops routing traffic to it. Requests are still served by the remaining node.
   
4. Start the Keycloak container again:
   ```bash
   docker start passthrough-keycloak1-1
   ```
5. Observe that Traefik automatically re-registers the backend and resumes routing traffic to it.

### 6. Stop the services

```bash
docker compose down
```

## Traefik configuration

The key parts of the Traefik configuration are explained below.

traefik.yaml — static configuration

**Entry point for TLS traffic:**
```yaml
entryPoints:
  websecure:
    address: ":8443"
```

Defines the entry point on port 8443 where Traefik accepts incoming TCP/TLS connections.

**Dashboard:**
```yaml
api:
  insecure: true
```
- `insecure: true` exposes the Traefik dashboard without any authentication. This is intended for local development and debugging only and must never be used in production.
Enables the Traefik dashboard on port 8080 without authentication (for local testing only). The dashboard is accessible at http://127.0.0.1:8080/dashboard/.

keycloak.yaml — dynamic configuration

**TCP router with TLS passthrough:**
```yaml
tcp:
  routers:
    keycloak:
      rule: "HostSNI(`*`)"
      entryPoints:
        - websecure
      tls:
        passthrough: true
      service: keycloak
```
- `HostSNI(*)`matches all TLS connections regardless of the SNI hostname.
- `tls.passthrough: true` instructs Traefik to forward the raw TLS stream without terminating it. Keycloak handles TLS termination.

**Load balancer with proxy protocol:**
```yaml
   services:
     keycloak-service:
       loadBalancer:
         serversTransport: kc-passthrough
         healthCheck:
           interval: "5s"
           timeout: "3s"
           port: 9000
         servers:
           - address: "keycloak1:8443"
           - address: "keycloak2:8443"
```
- `serversTransport: kc-passthrough` links the load balancer to the transport defined above, enabling PROXY protocol v2 on all backend connections. This requires Keycloak to be configured with KC_PROXY_PROTOCOL_ENABLED=true.
- Traffic is distributed across both Keycloak instances using round-robin.

**HTTP health check on the management port:**

Since Traefik is operating at the TCP layer for TLS passthrough, it performs health checks by sending a raw HTTP request and validating the response string.

```yaml
healthCheck:
  interval: "5s"
  timeout: "2s"
  port: 9000
  send: "GET /health/ready HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n"
  expect: "200"
```

Traefik performs health checks against Keycloak's management endpoint /health/ready, expecting an HTTP 200 response. This is achieved by sending a manual HTTP/1.1 request and looking for the "200" status string in the response buffer.
This endpoint is only available when Keycloak is configured with KC_HEALTH_ENABLED=true and KC_METRICS_ENABLED=true.

**Server lines:**

In the dynamic configuration, the servers and health parameters are defined within the TCP service:

```yaml
tcp:
  services:
    keycloak-service:
      loadBalancer:
        proxyProtocol:
          version: 2
        healthCheck:
          interval: "5s"
          timeout: "2s"
          port: 9000
        servers:
          - address: "keycloak1:8443"
          - address: "keycloak2:8443"
```
- `port: 9000`  directs health checks to the management port (9000). Because Traefik is in TCP mode, it sends the raw send string to this port to verify application readiness without interfering with the main TLS traffic on port 8443.
Version 1 (`send-proxy`) is also supported.
This requires Keycloak to be configured with `KC_PROXY_PROTOCOL_ENABLED=true`.

- `docker run --rm --network passthrough_backend alpine nc -zv keycloak1 9000`  verify the management port is reachable within the internal Docker network (as seen in the nc test)

## Resources

- [Traefik TCP Health Check Documentation](https://doc.traefik.io/traefik/routing/services/#health-check_1)
- [Traefik Proxy Protocol Configuration](https://doc.traefik.io/traefik/routing/services/#proxy-protocol)
- [Keycloak Guide - Reverse Proxy Configuration](https://www.keycloak.org/server/reverseproxy)