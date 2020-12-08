package org.keycloak.quickstart.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class ConsolePage {

    @FindBy(id = "user-name")
    private WebElement username;

    @FindBy(id = "signOutButton")
    private WebElement logoutLink;

    public void logout() {
        logoutLink.click();
    }

    public String getUser() {
        return username.getAttribute("value");
    }

}
