package org.keycloak.quickstart.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class ExternalActionPage {

    @FindBy(xpath = "//button[text()[contains(.,'Submit')]]")
    private WebElement submitButton;

    @FindBy(name = "field_1")
    private WebElement field1;

    @FindBy(name = "field_2")
    private WebElement field2;

    public void submit() {
        submitButton.click();
    }

    public void field1(String field1Text) {
        field1.clear();
        field1.sendKeys(field1Text);
    }

    public void field2(String field2Text) {
        field2.clear();
        field2.sendKeys(field2Text);
    }

}
