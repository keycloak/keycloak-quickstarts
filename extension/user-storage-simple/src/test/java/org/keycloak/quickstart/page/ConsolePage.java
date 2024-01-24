package org.keycloak.quickstart.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.waitAjax;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class ConsolePage {

    @FindBy(xpath = "//a[text() = 'Sign out']")
    private WebElement logoutLink;

    @FindBy(xpath = "//div[@data-testid='options']")
    private WebElement profileMenu;

    public void logout() {
        profileMenu.click();
        logoutLink.click();
    }

    public String getUser() {
        waitAjax().until().element(profileMenu).text().not().equalTo("");
        return profileMenu.getText();
    }

}
