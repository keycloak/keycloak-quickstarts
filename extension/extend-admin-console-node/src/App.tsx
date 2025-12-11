import KeycloakAdminClient from "@keycloak/keycloak-admin-client";
import {
  AccessContextProvider,
  AccountEnvironment,
  AdminClientContext,
  ErrorBoundaryProvider,
  Header,
  initAdminClient,
  RealmContextProvider,
  RecentRealmsProvider,
  ServerInfoProvider,
  SubGroups,
  useEnvironment,
  WhoAmIContextProvider,
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
      <ErrorBoundaryProvider>
        <RealmContextProvider>
          <ServerInfoProvider>
            <WhoAmIContextProvider>
              <RecentRealmsProvider>
                <AccessContextProvider>
                  <SubGroups>
                    <Page
                      className={style.headerLogo}
                      header={<Header />}
                      sidebar={<PageNav />}
                      isManagedSidebar
                    >
                      <PageSection variant={PageSectionVariants.darker}>
                        <a href="https://vitejs.dev" target="_blank">
                          <img
                            src={viteLogo}
                            className={style.logo}
                            alt="Vite logo"
                          />
                        </a>
                        <h1>extra content</h1>
                      </PageSection>
                      <Suspense fallback={<Spinner />}>
                        <Outlet />
                      </Suspense>
                    </Page>
                  </SubGroups>
                </AccessContextProvider>
              </RecentRealmsProvider>
            </WhoAmIContextProvider>
          </ServerInfoProvider>
        </RealmContextProvider>
      </ErrorBoundaryProvider>
    </AdminClientContext.Provider>
  );
}

export default App;
