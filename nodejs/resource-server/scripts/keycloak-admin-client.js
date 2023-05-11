import KcAdminClient from '@keycloak/keycloak-admin-client';
import config from '../config/config.js';

const adminClient = new KcAdminClient(config.adminClient)

await adminClient.auth(config.adminClient);

export default adminClient;