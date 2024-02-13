package org.keycloak.quickstart;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
}
