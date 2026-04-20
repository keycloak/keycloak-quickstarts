package org.keycloak.quickstart;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

        return "device-info";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("token", deviceFlowService.getAccessToken());
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout() {
        deviceFlowService.logout();
        return "redirect:/";
    }
}