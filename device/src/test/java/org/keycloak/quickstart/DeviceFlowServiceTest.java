package org.keycloak.quickstart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
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

    @Test
    void testPollTask_Success() {
        // 1. Prepare state
        deviceFlowService.setDeviceCode("DEV123");

        // 2. Mock /token success response
        String jsonResponse = """
                {
                    "access_token": "mock-access-token",
                    "expires_in": 3600
                }
                """;

        server.expect(requestTo("http://localhost:8080/realms/device-flow-quickstart/protocol/openid-connect/token"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().formDataContains(java.util.Map.of("device_code", "DEV123")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // 3. Execute
        deviceFlowService.pollTask();

        // 4. Verify
        assertEquals("mock-access-token", deviceFlowService.getAccessToken());
        assertNull(deviceFlowService.getLastError());
        server.verify();
    }

    @Test
    void testPollTask_AuthorizationPending_ShouldNotSetToken() {
        deviceFlowService.setDeviceCode("DEV123");

        // Keycloak returns 400 Bad Request with "authorization_pending" during polling
        String errorJson = "{\"error\": \"authorization_pending\"}";

        server.expect(requestTo("http://localhost:8080/realms/device-flow-quickstart/protocol/openid-connect/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(errorJson)
                        .contentType(MediaType.APPLICATION_JSON));

        deviceFlowService.pollTask();

        assertNull(deviceFlowService.getAccessToken());
        assertNull(deviceFlowService.getLastError(), "Pending should not set an error message yet");
        server.verify();
    }

    @Test
    void testPollTask_AccessDenied_ShouldStopPolling() {
        deviceFlowService.setDeviceCode("DEV123");

        String errorJson = "{\"error\": \"access_denied\"}";

        server.expect(requestTo("http://localhost:8080/realms/device-flow-quickstart/protocol/openid-connect/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(errorJson)
                        .contentType(MediaType.APPLICATION_JSON));

        deviceFlowService.pollTask();

        assertNull(deviceFlowService.getAccessToken());
        assertEquals("User denied the request.", deviceFlowService.getLastError());
        server.verify();
    }
}