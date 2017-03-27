package org.keycloak.quickstart.page;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by abstractj on 3/24/17.
 */
public class ProfilePage {

    @FindBy(name = "profileBtn")
    private WebElement profileButton;

    @FindBy(name = "tokenBtn")
    private WebElement tokenButton;

    @FindBy(name = "logoutBtn")
    private WebElement logoutButton;

    @FindBy(name = "accountBtn")
    private WebElement accountButton;

    @FindBy(id = "token-content")
    private WebElement tokenContent;

    @FindBy(id = "username")
    private WebElement username;

    public void clickProfile() {
        profileButton.click();
    }

    public void clickToken() {
        tokenButton.click();
    }

    public void clickLogout() {
        logoutButton.click();
    }

    public void clickAccount() {
        accountButton.click();
    }

    public String getUsername() {
        return username.getText();
    }

    public JsonObject getTokenContent() throws Exception {
        return new JsonParser().parse(tokenContent.getText()).getAsJsonObject();
    }

}

