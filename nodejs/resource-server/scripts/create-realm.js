import { readFileSync } from 'node:fs';
import adminClient from './keycloak-admin-client.js';

await adminClient.realms.create(
  JSON.parse(readFileSync(new URL('../config/realm-import.json', import.meta.url), 'utf8'))
);