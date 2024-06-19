import type { IndexRouteObject, RouteObject } from "react-router-dom";
import App from "./App";
import { environment } from "./environment";
import {
  Applications,
  DeviceActivity,
  Groups,
  LinkedAccounts,
  PersonalInfo,
  Resources,
  SigningIn,
} from "@keycloak/keycloak-account-ui";
import { MyPage } from "./MyPage";

export const DeviceActivityRoute: RouteObject = {
  path: "account-security/deviceActivity",
  element: <DeviceActivity />,
};

export const LinkedAccountsRoute: RouteObject = {
  path: "account-security/linkedAccounts",
  element: <LinkedAccounts />,
};

export const SigningInRoute: RouteObject = {
  path: "account-security/signingIn",
  element: <SigningIn />,
};

export const ApplicationsRoute: RouteObject = {
  path: "applications",
  element: <Applications />,
};

export const GroupsRoute: RouteObject = {
  path: "groups",
  element: <Groups />,
};

export const ResourcesRoute: RouteObject = {
  path: "resources",
  element: <Resources />,
};

export const PersonalInfoRoute: IndexRouteObject = {
  index: true,
  path: "personalInfo",
  element: <PersonalInfo />,
};

export const MyPageRoute: RouteObject = {
  path: "myPage",
  element: <MyPage />,
};

export const RootRoute: RouteObject = {
  path: decodeURIComponent(new URL(environment.baseUrl).pathname),
  element: <App />,
  errorElement: <>Error</>,
  children: [
    PersonalInfoRoute,
    DeviceActivityRoute,
    LinkedAccountsRoute,
    SigningInRoute,
    ApplicationsRoute,
    GroupsRoute,
    ResourcesRoute,
    MyPageRoute,
  ],
};

export const routes: RouteObject[] = [RootRoute];
