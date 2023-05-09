import config from './config.js';
import KcAdminClient from '@keycloak/keycloak-admin-client';
const adminClient = new KcAdminClient(config.adminClient)
async function auth() {
    return adminClient.auth(config.adminClient);
}

await auth();

export default adminClient;