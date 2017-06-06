package org.keycloak.quickstart.page;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class ConsolePage {

    @FindBy(partialLinkText = "User Federation")
    private WebElement userFederationLink;

    @FindBy(partialLinkText = "readonly-property-file")
    private WebElement readOnlyStorageLink;

    @FindBy(partialLinkText = "writeable-property-file")
    private WebElement writableStorageLink;

    @FindBy(xpath = "//select/option[normalize-space(text())='readonly-property-file']")
    private WebElement readOnlyStorageOption;

    @FindBy(xpath = "//select/option[normalize-space(text())='writeable-property-file']")
    private WebElement writableStorageOption;

    @FindBy(xpath = "//button[text()[contains(.,'Save')]]")
    private WebElement save;

    @FindBy(xpath = "//td[text()[contains(.,'Delete')]]")
    private WebElement deleteBtn;

    @FindBy(xpath = "//button[text()[contains(.,'Delete')]]")
    private WebElement deleteConfirmationBtn;

    @FindBy(id = "username")
    private WebElement username;

    @FindBy(linkText = "Sign Out")
    private WebElement logoutLink;

    @FindByJQuery("input[class*='form-control']:eq(3)")
    private WebElement propertyPath;

    public void navigateToUserFederationMenu() {
        Graphene.waitGui().until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("User Federation")));
        userFederationLink.click();
    }

    public void selectReadOnlyUserStorage() {
        readOnlyStorageOption.click();
    }

    public void selectWritableUserStorage() {
        writableStorageOption.click();
    }

    public void logout() {
        logoutLink.click();
    }

    public String getUser() {
        Graphene.waitGui().until(ExpectedConditions.visibilityOfElementLocated(
                By.id("username")));
        return username.getAttribute("value");
    }

    public WebElement readOnlyStorageLink() {
        return readOnlyStorageLink;
    }

    public WebElement writableStorageLink() {
        return writableStorageLink;
    }

    public void createReadOnlyStorage() {
        navigateToUserFederationMenu();
        selectReadOnlyUserStorage();
        save.click();
    }

    public void selectWritableStorage() {
        navigateToUserFederationMenu();
        selectWritableUserStorage();
    }

    public void save() {
        save.click();
    }

    public void setFileStoragePath(String path) {
        propertyPath.clear();
        propertyPath.sendKeys(path);
    }

    public void delete() {
        navigateToUserFederationMenu();
        deleteBtn.click();
        deleteConfirmationBtn.click();
    }

}
