import KeycloakAdminClient from "@keycloak/keycloak-admin-client";
import {
  AccountEnvironment,
  Header,
  useEnvironment,
  initAdminClient,
  AdminClientContext,
} from "@keycloak/keycloak-admin-ui";
import {
  Page,
  PageSection,
  PageSectionVariants,
  Spinner,
} from "@patternfly/react-core";
import { Suspense, useEffect, useState } from "react";
import { Outlet } from "react-router-dom";
import { PageNav } from "./PageNav";

import style from "./App.module.css";
import viteLogo from "/vite.svg";

function App() {
  const { keycloak, environment } = useEnvironment<AccountEnvironment>();
  const [adminClient, setAdminClient] = useState<KeycloakAdminClient>();

  useEffect(() => {
    const init = async () => {
      const client = await initAdminClient(keycloak, environment);
      setAdminClient(client);
    };
    init().catch(console.error);
  }, []);

  if (!adminClient) return <Spinner />;
  return (
    <AdminClientContext.Provider value={{ keycloak, adminClient }}>
      <Page
        className={style.headerLogo}
        header={<Header />}
        sidebar={<PageNav />}
        isManagedSidebar
      >
        <PageSection variant={PageSectionVariants.darker}>
          <a href="https://vitejs.dev" target="_blank">
            <img src={viteLogo} className={style.logo} alt="Vite logo" />
          </a>
          <h1>extra content</h1>
        </PageSection>
        <Suspense fallback={<Spinner />}>
          <Outlet />
        </Suspense>
      </Page>
    </AdminClientContext.Provider>
  );
}

export default App;
