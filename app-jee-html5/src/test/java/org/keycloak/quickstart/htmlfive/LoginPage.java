package org.keycloak.quickstart.htmlfive;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by sblanc on 2/1/17.
 */
public class LoginPage {


    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "totp")
    private WebElement totp;

    @FindBy(id = "rememberMe")
    private WebElement rememberMe;

    @FindBy(name = "login")
    private WebElement submitButton;

    @FindBy(name = "cancel")
    private WebElement cancelButton;

    @FindBy(linkText = "Register")
    private WebElement registerLink;

    @FindBy(linkText = "Forgot Password?")
    private WebElement resetPasswordLink;

    @FindBy(linkText = "Username")
    private WebElement recoverUsernameLink;

    @FindBy(className = "alert-error")
    private WebElement loginErrorMessage;

    @FindBy(className = "alert-warning")
    private WebElement loginWarningMessage;

    @FindBy(className = "alert-success")
    private WebElement loginSuccessMessage;


    @FindBy(className = "alert-info")
    private WebElement loginInfoMessage;

    @FindBy(className = "instruction")
    private WebElement instruction;


    @FindBy(id = "kc-current-locale-link")
    private WebElement languageText;

    @FindBy(id = "kc-locale-dropdown")
    private WebElement localeDropdown;

    public void login(String username, String password) {
        usernameInput.clear();
        usernameInput.sendKeys(username);

        passwordInput.clear();
        passwordInput.sendKeys(password);

        submitButton.click();
    }

    public void login(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);

        submitButton.click();
    }

    public void missingPassword(String username) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        submitButton.click();

    }
    public void missingUsername() {
        usernameInput.clear();
        submitButton.click();

    }

    public String getUsername() {
        return usernameInput.getAttribute("value");
    }

    public boolean isUsernameInputEnabled() {
        return usernameInput.isEnabled();
    }

    public String getPassword() {
        return passwordInput.getAttribute("value");
    }

    public void cancel() {
        cancelButton.click();
    }

    public String getError() {
        return loginErrorMessage != null ? loginErrorMessage.getText() : null;
    }

    public String getInstruction() {
        return instruction != null ? instruction.getText() : null;
    }

    public String getSuccessMessage() {
        return loginSuccessMessage != null ? loginSuccessMessage.getText() : null;
    }
    public String getInfoMessage() {
        return loginInfoMessage != null ? loginInfoMessage.getText() : null;
    }

    public void clickRegister() {
        registerLink.click();
    }

    public void resetPassword() {
        resetPasswordLink.click();
    }

    public void recoverUsername() {
        recoverUsernameLink.click();
    }

    public void setRememberMe(boolean enable) {
        boolean current = rememberMe.isSelected();
        if (current != enable) {
            rememberMe.click();
        }
    }

    public boolean isRememberMeChecked() {
        return rememberMe.isSelected();
    }
}
