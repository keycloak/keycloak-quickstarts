import { AccountEnvironment } from "@keycloak/keycloak-account-ui";
import { getInjectedEnvironment } from "@keycloak/keycloak-ui-shared";

export const environment = getInjectedEnvironment({
  authServerUrl: "http://localhost:8180",
  realm: "master",
  clientId: "security-admin-console-v2",
  resourceUrl: "http://localhost:8180",
  logo: "https://www.keycloak.org/img/logos/keycloak.svg",
  logoUrl: "https://www.keycloak.org/img/logos/keycloak.svg",
  baseUrl: "http://localhost:8080",
  locale: "en",
  features: {
    isRegistrationEmailAsUsername: false,
    isEditUserNameAllowed: true,
    isInternationalizationEnabled: true,
    isLinkedAccountsEnabled: true,
    isEventsEnabled: true,
    isMyResourcesEnabled: true,
    isTotpConfigured: true,
    deleteAccountAllowed: true,
    updateEmailFeatureEnabled: true,
    updateEmailActionEnabled: true,
    isViewGroupsEnabled: true,
    isOid4VciEnabled: false,
  },
} as AccountEnvironment);
