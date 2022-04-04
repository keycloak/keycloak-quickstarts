package org.keycloak.quickstart.uma.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.waitGui;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LogoutConfirmPage {

    @FindBy(css = "input[type=\"submit\"]")
    private WebElement confirmLogoutButton;

    @FindBy(className = "instruction")
    private WebElement infoMessage;

    public void confirmLogout() {
        waitGui().until().element(confirmLogoutButton).is().clickable();
        confirmLogoutButton.click();
    }
}
