package org.keycloak.quickstart.page;

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

    public void navigateToUserFederationMenu() {
        Graphene.waitGui().until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("User Federation")));
        userFederationLink.click();
    }

    public void selectUserStorage() {
        userStorageOption.click();
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
