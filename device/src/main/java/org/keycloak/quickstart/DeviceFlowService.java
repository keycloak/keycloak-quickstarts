package org.keycloak.quickstart;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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

    @Scheduled(fixedDelay = 1000)
    public void pollTask() {
        String code = currentDeviceCode.get();
        if (code == null || accessToken.get() != null) return;

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
            formData.add("client_id", "device-client");
            formData.add("device_code", code);

            TokenResponse response = restClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response != null && response.accessToken() != null) {
                this.accessToken.set(response.accessToken());
                this.currentDeviceCode.set(null);
            }
        } catch (HttpClientErrorException.BadRequest e) {
            TokenResponse errorResponse = e.getResponseBodyAs(TokenResponse.class);
            String error = errorResponse != null ? errorResponse.error() : "unknown_error";

            switch (error) {
                case "authorization_pending" -> { /* Keep polling */ }
                case "slow_down" -> System.out.println("Keycloak requested slower polling.");
                case "access_denied" -> stopPolling("User denied the request.");
                case "expired_token" -> stopPolling("The device code has expired.");
                default -> stopPolling("An error occurred: " + error);
            }
        }
    }

    private void stopPolling(String reason) {
        this.lastError.set(reason);
        this.currentDeviceCode.set(null);
    }


    public void logout() {
        this.accessToken.set(null);
        this.currentDeviceCode.set(null);
        this.lastError.set(null);
    }
}