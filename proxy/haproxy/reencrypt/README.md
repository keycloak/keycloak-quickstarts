# Keycloak HA with HAProxy TLS Re-encrypt

```bash
./generate-certs.sh 127.0.0.1.nip.io
```

```bash
KC_HOST=127.0.0.1.nip.io docker compose up -d
```

```bash
docker compose down -v
```

## Sticky Sessions

Keycloak creates an `AUTH_SESSION_ID` cookie with the format `<session-id>.<node-name>`.
HAProxy uses this cookie to route subsequent requests to the Keycloak node that owns the session:

```
use-server keycloak1 if { req.cook(AUTH_SESSION_ID) -m end keycloak1 }
use-server keycloak2 if { req.cook(AUTH_SESSION_ID) -m end keycloak2 }
```

Each `use-server` directive checks if the `AUTH_SESSION_ID` cookie value ends with (`-m end`) the given node name.
When it matches, HAProxy routes the request to that server, keeping the session local to the owning node and avoiding cross-node lookups in the Infinispan distributed cache.

For this to work, each Keycloak instance must have a stable and predictable node name that matches the condition in the `use-server` directive.
By default, Keycloak generates a random node identifier on startup, which would not match.
Setting `KC_SPI_CACHE_EMBEDDED__DEFAULT__NODE_NAME` overrides this with a fixed value:

```yaml
# In docker-compose.yaml
# keycloak1 service
KC_SPI_CACHE_EMBEDDED__DEFAULT__NODE_NAME: keycloak1
# keycloak2 service
KC_SPI_CACHE_EMBEDDED__DEFAULT__NODE_NAME: keycloak2
```

Now all requests with the `AUTH_SESSION_ID` cookie have the node name as a suffix.
For requests without it — such as the initial request before authentication starts, static resource loads, or API calls — the `use-server` conditions don't match and HAProxy falls back to `balance roundrobin`.

### Verifying Sticky Sessions

To verify sticky sessions are working, check the HAProxy access logs.
The log format includes the backend server name, so you can confirm that all requests within an authentication flow (e.g., `/realms/master/protocol/openid-connect/`) are routed to the same server.

The HAProxy stats page is also available at `http://localhost:8404/stats` and shows per-server request counts and session information.

For more details, see the [Enabling sticky sessions](https://www.keycloak.org/server/reverseproxy#_enable_sticky_sessions) section in the Keycloak reverse proxy guide.
