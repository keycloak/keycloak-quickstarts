package org.keycloak.quickstart;

import java.time.Duration;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ExtendedAccountPage {

    @FindBy(
            css = ".pf-v5-c-masthead__brand img"
    )
    private WebElement keycloakManLogo;
    @FindBy(
            xpath = "//a[@data-testid='content/keycloak-man']/ancestor::li[button/text()='Keycloak Man']"
    )
    private WebElement keycloakManContainer;
    @FindBy(
            xpath = "//a[@data-testid='content/sample-overview']"
    )
    private WebElement overviewHomeBtn;
    @FindBy(
            xpath = "//a[@data-testid='content/keycloak-man']"
    )
    private WebElement keycloakManAppBtn;
    @FindBy(
            xpath = "//a[@data-testid='content/keycloak-man-loves-jsx']"
    )
    private WebElement keycloakManLovesJsx;

    @Drone
    private WebDriver webDriver;

    private static final String ACCOUNT_URL = "/realms/quickstart/account/#/";

    public void clickOverviewHome() {
        this.overviewHomeBtn.click();
    }

    public void clickKeycloakManApp() {
        this.keycloakManAppBtn.click();
    }

    public void clickKeycloakManContainer() {
        this.keycloakManContainer.click();
    }

    public void clickKeycloakManLovesJsx() {
        this.keycloakManLovesJsx.click();
    }

    public void navigateTo() {
        webDriver.navigate().to(ExtendAccountConsoleTest.KEYCLOAK_URL + ACCOUNT_URL);
    }

    public boolean isLogoPresent() {
        return this.keycloakManLogo.isEnabled();
    }

    public boolean isOverviewPage() {
        return new WebDriverWait(webDriver, Duration.ofSeconds(5)).until(ExpectedConditions.urlMatches(".*/sample-overview"));
    }

    public boolean isKeycloakManPage() {
        return new WebDriverWait(webDriver, Duration.ofSeconds(5)).until(ExpectedConditions.urlMatches(".*/keycloak-man"));
    }
}
