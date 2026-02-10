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

    public void insertDescription(String description) {
        this.inputDescription.sendKeys(description);
    }

    public void setTemperatureConstraint(double value, String unit) {
        this.inputTemperatureValue.sendKeys(String.valueOf(value));
        this.selectTemperatureUnit.sendKeys(unit);
    }

    public boolean selectLoadItem(int loadItemId){
        try {
            var checkbox = this.tableLoadItems.findElement(By.xpath(".//tbody/tr[td[2][text()='" + loadItemId + "']]/td[1]/input"));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean selectDevice(int deviceId){
        try {
            var checkbox = this.tableDevices.findElement(By.xpath(".//tbody/tr[td[2][text()='" + deviceId + "']]/td[1]/input"));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsLoadItemId(int loadItemId) {
        try {
            this.tableLoadItems.findElement(By.xpath(".//tbody/tr[td[2][text()='" + loadItemId + "']]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsDeviceId(int deviceId) {
        try {
            this.tableDevices.findElement(By.xpath(".//tbody/tr[td[2][text()='" + deviceId + "']]"));
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
