package org.keycloak.quickstart.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by abstractj on 3/22/17.
 */
public class IndexPage {

    public static final String UNAUTHORIZED = "401 Unauthorized";
    public static final String MESSAGE_PUBLIC = "Message: public";

    @FindBy(name = "loginBtn")
    private WebElement loginButton;

    @FindBy(name = "logoutBtn")
    private WebElement logoutButton;

    @FindBy(name = "adminBtn")
    private WebElement adminButton;

    @FindBy(name = "publicBtn")
    private WebElement publicButton;

    @FindBy(name = "securedBtn")
    private WebElement securedBtn;

    public void clickLogin() {
        loginButton.click();
    }

    public void clickLogout() {
        logoutButton.click();
    }

    public void clickAdmin() {
        adminButton.click();
    }

    public void clickPublic() {
        publicButton.click();
    }

    public void clickSecured() {
        securedBtn.click();
    }
}
