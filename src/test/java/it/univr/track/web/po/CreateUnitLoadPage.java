package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CreateUnitLoadPage extends BasePageObject{

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

    public CreateShipmentPage toCreateShipmentPage() {
        this.linkBackShipmentCreation.click();
        return new CreateShipmentPage(this.driver);
    }

    public ShipmentListPage toShipmentListPage() {
        this.linkShipments.click();
        return new ShipmentListPage(this.driver);
    }

    public String getTitle() {
        return this.h1CreateUnitLoad.getText();
    }

    public record LoadItemRow(boolean selected, long id, String description) {
    }

     public record DeviceRow(boolean selected, long id, String origin, boolean connected, boolean calibrated, boolean state, double price) {
    }

    public record PhysicalConstraint(String type, String value, String unit) {
    }

    public int getLoadItemsCount() {
        return this.tableLoadItems.findElements(org.openqa.selenium.By.xpath(".//tbody/tr")).size();
    }

    public int getDevicesCount() {
        return this.tableDevices.findElements(org.openqa.selenium.By.xpath(".//tbody/tr")).size();
    }

    public void setDescription(String description) {
        this.inputDescription.clear();
        this.inputDescription.sendKeys(description);
    }

    public String getDescription(){
        return this.inputDescription.getAttribute("value");
    }

    public void setTemperatureConstraint(double value, String unit) {
        this.inputTemperatureValue.sendKeys(String.valueOf(value));
        this.selectTemperatureUnit.sendKeys(unit);
    }

    public void setHumidityConstraint(double value) {
        this.inputHumidityValue.click();
        this.inputHumidityValue.clear();
        this.inputHumidityValue.sendKeys(String.valueOf(value));
    }

    public String getHumidityConstraintValue(){
        return this.inputHumidityValue.getAttribute("value").trim();
    }


    public boolean selectLoadItem(Long loadItemId) {
        try {
            String xpath = ".//tbody/tr[normalize-space(td[2])='" + loadItemId + "']//td[1]//input[@type='checkbox']";
            WebElement checkbox = tableLoadItems.findElement(By.xpath(xpath));

            if (!checkbox.isSelected()) checkbox.click();
            return true;

        } catch (Exception e) {
            System.out.println("DEBUG tableLoadItems text:\n" + tableLoadItems.getText());
            return false;
        }
    }



    public boolean selectDevice(Long deviceId) {
        try {
            var checkbox = tableDevices.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[2])='" + deviceId + "']//td[1]//input[@type='checkbox']"
            ));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean containsLoadItemId(Long loadItemId) {
        try {
            tableLoadItems.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[2])='" + loadItemId + "']"
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean containsDeviceId(Long deviceId) {
        try {
            this.tableDevices.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[2])='" + deviceId + "']"
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public CreateShipmentPage clickCreateUnitLoad() {
        this.buttonCreateUnitLoad.click();
        return new CreateShipmentPage(this.driver);
    }

}
