import { Page, PageSection, PageSectionVariants, Spinner } from "@patternfly/react-core";
import style from "./App.module.css";

import { Header } from "@keycloak/keycloak-account-ui";
import { Suspense } from "react";
import { Outlet } from "react-router-dom";
import { PageNav } from "./PageNav";
import viteLogo from "/vite.svg";

function App() {
  return (
    <Page className={style.headerLogo} header={<Header />} sidebar={<PageNav />} isManagedSidebar>
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
  );
}

export default App;
