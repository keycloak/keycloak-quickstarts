package org.keycloak.quickstart.springboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistration;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.representations.idm.ClientInitialAccessCreatePresentation;
import org.keycloak.representations.idm.ClientInitialAccessPresentation;
import org.keycloak.test.FluentTestsHelper;

public class SpringTestHelper extends FluentTestsHelper {

    public static final String KEYCLOAK_URL = "http://localhost:8180/auth";

    public SpringTestHelper() {
        super(KEYCLOAK_URL, DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, DEFAULT_ADMIN_REALM, DEFAULT_ADMIN_CLIENT, DEFAULT_TEST_REALM);
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
