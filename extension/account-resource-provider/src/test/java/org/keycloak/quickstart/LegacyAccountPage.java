//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.keycloak.quickstart;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LegacyAccountPage {

    @FindBy(
        linkText = "Account"
    )
    private WebElement accountLink;
    @FindBy(
        linkText = "Password"
    )
    private WebElement passwordLink;

    @Drone
    private WebDriver webDriver;

    private static final String ACCOUNT_URL = "/realms/quickstart/account/";

    public void clickAccount() {
        this.accountLink.click();
    }

    public void clickPassword() {
        this.passwordLink.click();
    }

    public void navigateTo() {
        webDriver.navigate().to(LegacyAccountConsoleTest.KEYCLOAK_URL + ACCOUNT_URL);
    }

    public boolean isAccountPage() {
        return webDriver.getCurrentUrl().endsWith("/account/");
    }

    public boolean isPasswordPage() {
        return webDriver.getCurrentUrl().endsWith("/account/password");
    }
}
