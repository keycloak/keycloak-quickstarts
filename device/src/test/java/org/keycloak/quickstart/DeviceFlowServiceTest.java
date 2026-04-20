package org.keycloak.quickstart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(DeviceFlowService.class)
class DeviceFlowServiceTest {

    @Autowired
    private DeviceFlowService deviceFlowService;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @Test
    void testInitiateDeviceFlow_Success() {
        String jsonResponse = """
                {
                    "device_code": "TEST123",
                    "user_code": "ABCD-EFGH",
                    "verification_uri": "http://localhost:8080/auth",
                    "verification_uri_complete": "http://localhost:8080/auth?code=ABCD-EFGH",
                    "expires_in": 600,
                    "interval": 5
                }
                """;

        server.expect(requestTo("http://localhost:8080/realms/device-flow-quickstart/protocol/openid-connect/auth/device"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DeviceResponse response = deviceFlowService.initiateDeviceFlow();

        assertNotNull(response);
        assertEquals("TEST123", response.deviceCode());
        assertEquals("ABCD-EFGH", response.userCode());
        server.verify();
    }

}