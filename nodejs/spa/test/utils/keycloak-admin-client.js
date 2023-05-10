import KcAdminClient from '@keycloak/keycloak-admin-client';
import config from './config.js';

const adminClient = new KcAdminClient(config.adminClient)
async function auth() {
    return adminClient.auth(config.adminClient);
}

await auth();

export default adminClient;