package org.keycloak.quickstart.springboot;

import org.keycloak.representations.idm.ClientInitialAccessCreatePresentation;
import org.keycloak.representations.idm.ClientInitialAccessPresentation;
import org.keycloak.test.FluentTestsHelper;

public class SpringTestHelper extends FluentTestsHelper {

    public static final String KEYCLOAK_URL = "http://localhost:8180/auth";

    public SpringTestHelper() {
        super(KEYCLOAK_URL, DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, DEFAULT_ADMIN_REALM, DEFAULT_ADMIN_CLIENT, DEFAULT_TEST_REALM);
    }

    public SpringTestHelper(String username, String password, String realm, String clientId) {
        super(KEYCLOAK_URL, username, password, realm, clientId, realm);
    }

    public SpringTestHelper initWithoutInitialToken() {
        keycloak = getKeycloakInstance(keycloakBaseUrl, adminRealm, adminUserName, adminPassword, adminClient);
        isInitialized = true;
        return this;
    }

    @Override
    protected String generateInitialAccessToken() {
        ClientInitialAccessCreatePresentation rep = new ClientInitialAccessCreatePresentation();
        rep.setCount(3);
        rep.setExpiration(180);
        ClientInitialAccessPresentation initialAccess = keycloak.realms().realm(testRealm).clientInitialAccess().create(rep);
        return initialAccess.getToken();
    }
}
