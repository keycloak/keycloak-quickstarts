import type { RouteObject } from "react-router-dom";
import { useRouteError } from "react-router-dom";
import App from "./App";
import { environment } from "./environment";
import { MyComponent } from "./MyComponent";

function ErrorBoundary() {
  const error = useRouteError() as Error;
  console.error("Router error:", error);
  return (
    <div style={{ padding: "20px" }}>
      <h1>Error</h1>
      <p>{error?.message || String(error)}</p>
      <pre>{error?.stack}</pre>
    </div>
  );
}

export const ClientsSectionRoute: RouteObject = {
  path: "clients",
  element: <MyComponent />,
};

export const RootRoute: RouteObject = {
  // @ts-ignore
  path: environment.consoleBaseUrl,
  element: <App />,
  errorElement: <ErrorBoundary />,
  children: [
    ClientsSectionRoute,
  ],
};

export const routes: RouteObject[] = [RootRoute];
