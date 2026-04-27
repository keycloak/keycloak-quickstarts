package org.keycloak.quickstart;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DeviceController {

    private final DeviceFlowService deviceFlowService;

    public DeviceController(DeviceFlowService deviceFlowService) {
        this.deviceFlowService = deviceFlowService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/login-device")
    public String startDeviceFlow(Model model) {
        // Call Keycloak
        DeviceResponse response = deviceFlowService.initiateDeviceFlow();

        deviceFlowService.setDeviceCode(response.deviceCode());
        // Pass the Keycloak data to the HTML page
        model.addAttribute("userCode", response.userCode());
        model.addAttribute("verificationUri", response.verificationUri());
        model.addAttribute("verificationUriComplete", response.verificationUriComplete());

        return "device-info";
    }

    @GetMapping("/check-status")
    @ResponseBody
    public Map<String, String> checkStatus() {
        String token = deviceFlowService.getAccessToken();
        String error = deviceFlowService.getLastError();

        if (token != null) {
            return Map.of("state", "success");
        } else if (error != null) {
            return Map.of("state", "error", "message", error);
        }
        return Map.of("state", "pending");
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("token", deviceFlowService.getAccessToken());
        return "welcome";
    }

    @PostMapping("/logout")
    public String logout() {
        deviceFlowService.logout();
        return "redirect:/";
    }
}