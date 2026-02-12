package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateUnitLoadPage extends BasePageObject {

    @FindBy(xpath = "//a[@href='/web/newShipment']")
    public WebElement linkBackShipmentCreation;

    @FindBy(xpath = "//a[@href='/web/shipments/list']")
    public WebElement linkShipments;

    @FindBy(id = "error")
    public WebElement pError;

    @FindBy(xpath = "//h1")
    public WebElement h1CreateUnitLoad;

    @FindBy(id = "description-input")
    public WebElement inputDescription;

    @FindBy(id = "temperature-value-input")
    public WebElement inputTemperatureValue;

    @FindBy(id = "temperature-unit-select")
    public WebElement selectTemperatureUnit;

    @FindBy(id = "humidity-value-input")
    public WebElement inputHumidityValue;

    @FindBy(id = "load-items-table")
    public WebElement tableLoadItems;

    @FindBy(id = "devices-table")
    public WebElement tableDevices;

    @FindBy(xpath = "//input[@type='submit']")
    public WebElement buttonCreateUnitLoad;

    public CreateUnitLoadPage(WebDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return this.h1CreateUnitLoad.getText();
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

    public void setHumidityConstraint(double value) {
        this.inputHumidityValue.click();
        this.inputHumidityValue.clear();
        this.inputHumidityValue.sendKeys(String.valueOf(value));
    }

    public String getHumidityConstraintValue() {
        return this.inputHumidityValue.getAttribute("value").trim();
    }

    public boolean selectLoadItemByDescription(String loadItemName) {
        try {
            String xpath = ".//tbody/tr[normalize-space(td[3])='" + loadItemName + "']//td[1]//input[@type='checkbox']";
            WebElement checkbox = tableLoadItems.findElement(By.xpath(xpath));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean selectDeviceByOrigin(String origin) {
        try {
            String xpath = ".//tbody/tr[normalize-space(td[3])='" + origin + "']//td[1]//input[@type='checkbox']";
            WebElement checkbox = tableDevices.findElement(By.xpath(xpath));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsDeviceOrigin(String origin) {
        try {
            this.tableDevices.findElement(By.xpath(".//tbody/tr[normalize-space(td[3])='" + origin + "']"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void submit() {
        this.buttonCreateUnitLoad.click();
    }
}
