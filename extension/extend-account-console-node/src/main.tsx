import "@patternfly/react-core/dist/styles/base.css";

import { KeycloakProvider } from "@keycloak/keycloak-account-ui";
import React from "react";
import ReactDOM from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { environment } from "./environment";
import { i18n } from "./i18n";
import { routes } from "./routes";

const router = createBrowserRouter(routes);

i18n.init().then(() => {
  ReactDOM.createRoot(document.getElementById("app")!).render(
    <React.StrictMode>
      <KeycloakProvider environment={environment}>
        <RouterProvider router={router} />
      </KeycloakProvider>
    </React.StrictMode>
  );
});
