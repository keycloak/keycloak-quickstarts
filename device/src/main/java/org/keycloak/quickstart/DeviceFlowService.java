package org.keycloak.quickstart;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class DeviceFlowService {

    private final RestClient restClient;
    private final AtomicReference<String> currentDeviceCode = new AtomicReference<>();
    private final AtomicReference<String> accessToken = new AtomicReference<>();
    private final AtomicReference<String> lastError = new AtomicReference<>();

    public DeviceFlowService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080/realms/device-flow-quickstart/protocol/openid-connect")
                .build();
    }

    public void setDeviceCode(String code) {
        this.currentDeviceCode.set(code);
        this.accessToken.set(null);
        this.lastError.set(null);
    }

    public String getAccessToken() { return accessToken.get(); }
    public String getLastError() { return lastError.get(); }

    public DeviceResponse initiateDeviceFlow() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "device-client");
        formData.add("scope", "openid profile");

        return restClient.post()
                .uri("/auth/device")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(DeviceResponse.class);
    }

    public void logout() {
        this.accessToken.set(null);
        this.currentDeviceCode.set(null);
        this.lastError.set(null);
    }
}