package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreatePackagePage extends BasePageObject {

    @FindBy(xpath = "//h1")
    public WebElement h1CreatePackage;

    @FindBy(xpath = "//a[@href='/web/newShipment']")
    public WebElement linkBackShipmentCreation;

    @FindBy(xpath = "//a[@href='/web/shipments/list']")
    public WebElement linkShipments;

    @FindBy(id = "error")
    public WebElement pError;

    @FindBy(id = "unit-loads-table")
    public WebElement tableUnitLoads;

    @FindBy(id = "description-input")
    public WebElement inputDescription;

    @FindBy(xpath = "//input[@type='submit' and @value='Create Package']")
    public WebElement buttonCreatePackage;

    public CreatePackagePage(WebDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return this.h1CreatePackage.getText();
    }

    public boolean hasError() {
        try {
            return pError != null && pError.isDisplayed() && !pError.getText().trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getError() {
        return hasError() ? pError.getText() : null;
    }

    public void setDescription(String description) {
        this.inputDescription.clear();
        this.inputDescription.sendKeys(description);
    }

    public String getDescription() {
        return this.inputDescription.getAttribute("value");
    }

    public boolean selectUnitLoadByDescription(String unitLoadDescription) {
        try {
            String xpath = ".//tbody/tr[normalize-space(td[3])='" + unitLoadDescription + "']//td[1]//input[@type='checkbox' and @name='unitLoadIds']";
            WebElement checkbox = tableUnitLoads.findElement(By.xpath(xpath));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void submit() {
        this.buttonCreatePackage.click();
    }
}
