//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.keycloak.quickstart;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ExtendedAccountPage {

    @FindBy(
            css = "#landingLogo > img[src*=keycloak-man]"
    )
    private WebElement keycloakManLogo;
    @FindBy(
            id = "landing-keycloak-man"
    )
    private WebElement keycloakManContainer;
    @FindBy(
            id = "landing-sample-overview"
    )
    private WebElement overviewHomeBtn;
    @FindBy(
            id = "nav-link-who-is-keycloak-man"
    )
    private WebElement keycloakManAppBtn;

    @Drone
    private WebDriver webDriver;

    private static final String ACCOUNT_URL = "/realms/quickstart/account/#/";

    public void clickOverviewHome() {
        this.overviewHomeBtn.click();
    }

    public void clickKeycloakManApp() {
        this.keycloakManAppBtn.click();
    }

    public void navigateTo() {
        webDriver.navigate().to(ExtendAccountConsoleTest.KEYCLOAK_URL + ACCOUNT_URL);
    }

    public boolean isLogoPresent() {
        return this.keycloakManLogo.isEnabled();
    }

    public boolean isOverviewPage() {
        return webDriver.getCurrentUrl().endsWith("/sample-overview");
    }

    public boolean isKeycloakManPage() {
        return webDriver.getCurrentUrl().endsWith("/keycloak-man");
    }
}
