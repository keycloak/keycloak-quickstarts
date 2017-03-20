package org.keycloak.quickstart.page;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.Assert.assertTrue;
import static org.keycloak.quickstart.utils.WaitUtils.waitTextToBePresent;

/**
 * Created by abstractj on 3/22/17.
 */
public class IndexPage {

    private static final String UNAUTHORIZED = "401 Unauthorized";
    public static final String MESSAGE_PUBLIC = "Message: public";

    @FindBy(name = "loginBtn")
    private WebElement loginButton;

    @FindBy(name = "adminBtn")
    private WebElement adminButton;

    @FindBy(name = "publicBtn")
    private WebElement publicButton;

    @FindBy(name = "securedBtn")
    private WebElement securedBtn;

    @FindBy(id = "message")
    private WebElement message;

    public void clickLogin() {
        loginButton.click();
    }

    public void clickAdmin() {
        adminButton.click();
    }

    public void clickPublic() {
        publicButton.click();
    }

    public String getMessage() {
        return message.getText();
    }

    public boolean isUnauthorized(WebDriver webDriver) {
        waitTextToBePresent(webDriver, By.className("error"), UNAUTHORIZED);
        return message.getText().contains(UNAUTHORIZED);
    }

    public boolean isPublic(WebDriver webDriver) {
        waitTextToBePresent(webDriver, By.id("message"), "Message: public");
        return message.getText().contains(MESSAGE_PUBLIC);
    }

    public void clickSecured() {
        securedBtn.click();
    }
}
