package org.keycloak.quickstart.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class AuthzPage {

    @FindBy(name = "logoutBtn")
    private WebElement logoutButton;

    @FindBy(tagName = "h3")
    private WebElement message;

    public void clickLogout() {
        logoutButton.click();
    }

    public String getMessage() {
        return message.getText();
    }
}