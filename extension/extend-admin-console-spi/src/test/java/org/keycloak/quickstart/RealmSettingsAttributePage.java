package org.keycloak.quickstart;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.jboss.arquillian.graphene.Graphene;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static org.keycloak.quickstart.ExtendedAdminPage.ADMIN_CONSOLE;

public class RealmSettingsAttributePage {

    @FindBy(
            xpath = "//input[@data-testid='logo']"
    )
    private WebElement logoInput;

    @FindBy(
            xpath = "//button[@data-testid='save']"
    )
    private WebElement saveButton;

    @FindBy(
            xpath = "//button[@data-testid='cancel']"
    )
    private WebElement revertButton;

    @FindBy(
            css = ".pf-m-success"
    )
    private WebElement alert;

    @Drone
    private WebDriver webDriver;
    public void navigateTo() {
        webDriver.navigate().to(ExtendAdminConsoleTest.KEYCLOAK_URL + ADMIN_CONSOLE + "#/master/realm-settings/attributes");
    }

    public boolean logoInputExists() {
        return logoInput.isEnabled();
    }

    public void saveLogoField(String logo) {
        logoInput.sendKeys(logo);
        saveButton.click();
    }

    public boolean isSaved() {
        return alert.isEnabled();
    }

    public void fillLogoField(String logo) {
        logoInput.clear();
        logoInput.sendKeys(logo);
    }

    public String getLogoFieldValue() {
        return logoInput.getAttribute("value");
    }

    public void clickRevertButton() {
        // 1. Wait until the button is fully interactive and ready for input
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(revertButton));

        // 2. Perform the click
        revertButton.click();
    }

    // Example for RealmSettingsAttributePage
    public boolean isRevertButtonPresent() {
        try {
            // Wait up to 5 or 10 seconds for the element to physically appear in the DOM
            Graphene.waitModel().withTimeout(5, TimeUnit.SECONDS)
                    .until().element(revertButton).is().present();
            return revertButton.isDisplayed();
        } catch (Exception e) {
            // Element not found or timeout - this is expected when button is not present
            return false;
        }
    }
}
