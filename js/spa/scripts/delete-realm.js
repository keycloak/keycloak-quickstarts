import adminClient from './keycloak-admin-client.js';

await adminClient.realms.del({ realm : 'quickstart' });