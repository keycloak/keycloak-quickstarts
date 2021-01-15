package org.keycloak.quickstart.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.waitAjax;

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
        waitAjax().until().element(username).value().not().equalTo("");
        return username.getAttribute("value");
    }

}
