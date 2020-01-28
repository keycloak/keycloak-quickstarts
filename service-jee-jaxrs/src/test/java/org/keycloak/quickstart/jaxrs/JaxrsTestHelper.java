package org.keycloak.quickstart.jaxrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistration;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.test.FluentTestsHelper;

public class JaxrsTestHelper extends FluentTestsHelper {

    public static final String KEYCLOAK_URL = "http://localhost:8180/auth";

    public JaxrsTestHelper() {
        super(KEYCLOAK_URL, DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, DEFAULT_ADMIN_REALM, DEFAULT_ADMIN_CLIENT, DEFAULT_TEST_REALM);
    }

    public JaxrsTestHelper(String username, String password, String realm, String clientId) {
        super(KEYCLOAK_URL, username, password, realm, clientId, realm);
    }

    public JaxrsTestHelper initWithoutInitialToken() {
        keycloak = getKeycloakInstance(keycloakBaseUrl, adminRealm, adminUserName, adminPassword, adminClient);
        isInitialized = true;
        return this;
    }

    public String getAdapterConfiguration(String clientId) throws ClientRegistrationException, JsonProcessingException {
        assert isInitialized;
        ClientData clientData = createdClients.get(clientId);
        if (clientData == null) {
            throw new ClientRegistrationException("This client wasn't created by this helper!");
        }
        ClientRegistration reg = ClientRegistration.create()
                .url(keycloakBaseUrl, testRealm)
                .build();
        reg.auth(Auth.token(clientData.getRegistrationCode()));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(reg.getAdapterConfig(clientId));
    }
}
