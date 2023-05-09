import fs from 'node:fs'
import adminClient from './keycloak-admin-client.js';

await adminClient.realms.create(JSON.parse(fs.readFileSync('config/realm-import.json', 'utf8'))).catch(err => {});