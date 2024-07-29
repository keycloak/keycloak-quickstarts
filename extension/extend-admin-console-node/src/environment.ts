import { AccountEnvironment } from "@keycloak/keycloak-account-ui";
import { getInjectedEnvironment } from "@keycloak/keycloak-ui-shared";

export const environment = getInjectedEnvironment<AccountEnvironment>();
