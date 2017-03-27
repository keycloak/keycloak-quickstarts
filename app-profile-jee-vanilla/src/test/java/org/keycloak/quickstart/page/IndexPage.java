package org.keycloak.quickstart.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by abstractj on 3/22/17.
 */
public class IndexPage {

    @FindBy(name = "loginBtn")
    private WebElement loginButton;

    public void clickLogin() {
        loginButton.click();
    }
}
