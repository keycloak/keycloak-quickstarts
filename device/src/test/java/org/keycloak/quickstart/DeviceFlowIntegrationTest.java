package org.keycloak.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class DeviceFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeviceFlowService deviceFlowService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void testFullLoginAndStatusCheckFlow() throws Exception {

        String deviceJson = """
                {
                    "device_code": "test_device_code",
                    "user_code": "ABCD-1234",
                    "verification_uri": "http://localhost:8080/auth",
                    "verification_uri_complete": "http://localhost:8080/auth?code=ABCD-1234"
                }
                """;
        mockServer.expect(requestTo("http://localhost:8080/realms/device-flow-quickstart/protocol/openid-connect/auth/device"))
                .andRespond(withSuccess(deviceJson, MediaType.APPLICATION_JSON));

        String tokenJson = """
                {
                    "access_token": "mock-jwt-token",
                    "expires_in": 3600
                }
                """;
        mockServer.expect(requestTo("http://localhost:8080/realms/device-flow-quickstart/protocol/openid-connect/token"))
                .andRespond(withSuccess(tokenJson, MediaType.APPLICATION_JSON));


        mockMvc.perform(post("/login-device"))
                .andExpect(status().isOk())
                .andExpect(view().name("device-info"))
                .andExpect(model().attribute("userCode", "ABCD-1234"));

        deviceFlowService.pollTask();

        mockMvc.perform(get("/check-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("success"));

        mockServer.verify();
    }

    @Test
    void testLogoutClearsSession() throws Exception {

        deviceFlowService.setDeviceCode("some_code");
        ReflectionTestUtils.setField(deviceFlowService, "accessToken", new AtomicReference<>("fake-token"));

        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/check-status"))
                .andExpect(jsonPath("$.state").value("pending"));
    }
}