package org.keycloak.quickstart.page;

import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class ConsolePage {

    @FindBy(partialLinkText = "User Federation")
    private WebElement userFederationLink;

    @FindBy(partialLinkText = "example-user-storage-jpa")
    private WebElement exampleFederationStorageLink;

    @FindBy(xpath = "//select/option[normalize-space(text())='example-user-storage-jpa']")
    private WebElement userStorageOption;

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

    public void navigateToUserFederationMenu() {
        Graphene.waitGui().withTimeout(30, TimeUnit.SECONDS).until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("User Federation")));
        userFederationLink.click();
    }

    public void selectUserStorage() {
        Graphene.waitGui().withTimeout(30, TimeUnit.SECONDS).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//select/option[normalize-space(text())='example-user-storage-jpa']")));
        userStorageOption.click();
    }

    public String getUser() {
        Graphene.waitGui().withTimeout(30, TimeUnit.SECONDS).until(ExpectedConditions.visibilityOfElementLocated(
                By.id("username")));
        return username.getAttribute("value");
    }

    public void logout() {
        logoutLink.click();
    }

    public WebElement exampleFederationStorageLink() {
        return exampleFederationStorageLink;
    }

    public void save() {
        save.click();
    }

    public void delete() {
        deleteBtn.click();
        deleteConfirmationBtn.click();
    }

}
