import { ClientsSection } from "@keycloak/keycloak-admin-ui";
import { KeycloakSpinner } from "@keycloak/keycloak-ui-shared";
import { Suspense } from "react";

export const MyComponent = () => {
  return (
    <div>
      <h1>My Clients Section</h1>
      <Suspense fallback={<KeycloakSpinner />}>
        {ClientsSection.default()}
      </Suspense>
    </div>
  );
};
