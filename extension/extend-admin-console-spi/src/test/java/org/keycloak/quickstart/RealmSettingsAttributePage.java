package org.keycloak.quickstart;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.By;
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

    private static final By REVERT_BUTTON = By.xpath("//button[@data-testid='cancel']");

    public void clickRevertButton() {
        // Click the visible revert button; bind to the visible match rather than the
        // @FindBy proxy's first match, which may be a not-yet-visible element.
        WebElement button = new WebDriverWait(webDriver, Duration.ofSeconds(15))
                .until(d -> d.findElements(REVERT_BUTTON).stream()
                        .filter(WebElement::isDisplayed).findFirst().orElse(null));
        button.click();
    }

    public boolean isRevertButtonPresent() {
        try {
            // Wait until at least one matching button is actually visible (not merely
            // present in the DOM). This is resilient to slow SPA rendering under CI load.
            new WebDriverWait(webDriver, Duration.ofSeconds(15))
                    .until(d -> d.findElements(REVERT_BUTTON).stream()
                            .anyMatch(WebElement::isDisplayed));
            return true;
        } catch (Exception e) {
            // Element not found or timeout - the button is not present in this build.
            return false;
        }
    }
}
