import { ClientsSection } from "@keycloak/keycloak-admin-ui";
import type { RouteObject } from "react-router-dom";
import App from "./App";
import { environment } from "./environment";

export const ClientsSectionRoute: RouteObject = {
  path: "clients",
  element: ClientsSection.default(),
};

export const RootRoute: RouteObject = {
  path: decodeURIComponent(new URL(environment.serverBaseUrl).pathname),
  element: <App />,
  errorElement: <>Error</>,
  children: [
    ClientsSectionRoute,
  ],
};

export const routes: RouteObject[] = [RootRoute];
