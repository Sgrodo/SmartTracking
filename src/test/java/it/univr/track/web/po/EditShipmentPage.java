package it.univr.track.web.po;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class EditShipmentPage extends BasePageObject {

    @FindBy(xpath = "//h1")
    public WebElement h1EditShipment;

    @FindBy(xpath = "//h1")
    public WebElement h1;

    @FindBy(id = "errorMessage")
    public WebElement error;

    @FindBy(name = "fromLocation")
    public WebElement inputFrom;

    @FindBy(name = "toLocation")
    public WebElement inputTo;

    @FindBy(id = "errorMessage")
    public WebElement pErrorMessage;


    @FindBy(id = "error")
    public WebElement pError;

    @FindBy(xpath = "//span")
    public WebElement spanShipmentId;

    @FindBy(xpath = "//input[@id='description-input']")
    public WebElement inputDescription;

    @FindBy(id = "unit-loads-table")
    public WebElement tableUnitLoads;

    @FindBy(id = "packages-table")
    public WebElement tablePackages;

    @FindBy(xpath = "//button[@type='submit' or @type='submit'] | //input[@type='submit']")
    public WebElement buttonSave;

    @FindBy(xpath = "//button[@type='button']")
    public WebElement buttonCancel;

    public EditShipmentPage(WebDriver driver) {
        super(driver);
    }
    
    public String getTitle() {
        return this.h1EditShipment.getText();
    }
    
    public String getDescription() {
        return this.inputDescription.getAttribute("value");
    }

    public void insertDescription(String description) {
        this.inputDescription.clear();
        this.inputDescription.sendKeys(description);
    }
    
    public List<CreateShipmentPage.UnitLoadRow> getSelectedUnitLoads() {
        return this.tablePackages.findElements(By.xpath(".//tbody/tr")).stream()
                .filter(tr -> tr.findElements(By.tagName("td")).size() == 4)
                .map(tr -> {
                    var columns = tr.findElements(By.tagName("td"));
                    var selected = Boolean.parseBoolean(columns.get(0).findElement(By.xpath(".//input[@type='checkbox']")).getAttribute("value"));
                    var id = Long.parseLong(columns.get(1).getText());
                    var description = columns.get(2).getText();
                    var numPhysicalConstraints = Long.parseLong(columns.get(3).getText());
                    return new CreateShipmentPage.UnitLoadRow(selected, id, description, numPhysicalConstraints);
                })
                .toList();
    }

    public int getUnitLoadsCount() {
        return this.tableUnitLoads.findElements(By.xpath(".//tbody/tr")).size();
    }

    public boolean selectUniLoad(int unitLoadId){
        try {
            var checkbox = this.tableUnitLoads.findElement(By.xpath(".//tbody/tr[td[2][text()='" + unitLoadId + "']]/td[1]/input"));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deselectUniLoad(int unitLoadId){
        try {
            var checkbox = this.tableUnitLoads.findElement(By.xpath(".//tbody/tr[td[2][text()='" + unitLoadId + "']]/td[1]/input"));
            if (checkbox.isSelected()) {
                checkbox.click();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUniLoadSelected(int unitLoadId) {
        try {
            var checkbox = this.tableUnitLoads.findElement(By.xpath(".//tbody/tr[td[2][text()='" + unitLoadId + "']]/td[1]/input"));
            return checkbox.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public List<CreateShipmentPage.PackageRow> getSelectedPackages() {
        return this.tableUnitLoads.findElements(By.xpath(".//tbody/tr")).stream()
                .filter(tr -> tr.findElements(By.tagName("td")).size() == 4)
                .map(tr -> {
                    var columns = tr.findElements(By.tagName("td"));
                    var selected = Boolean.parseBoolean(columns.get(0).findElement(By.xpath(".//input[@type='checkbox']")).getAttribute("value"));
                    var id = Long.parseLong(columns.get(1).getText());
                    var description = columns.get(2).getText();
                    var numUnitLoads = Long.parseLong(columns.get(3).getText());
                    return new CreateShipmentPage.PackageRow(selected, id, description, numUnitLoads);
                })
                .toList();
    }

    public boolean selectPackage(int packageId){
        try {
            var checkbox = this.tableUnitLoads.findElement(By.xpath(".//tbody/tr[td[2][text()='" + packageId + "']]/td[1]/input"));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deselectPackage(int packageId){
        try {
            var checkbox = this.tableUnitLoads.findElement(By.xpath(".//tbody/tr[td[2][text()='" + packageId + "']]/td[1]/input"));
            if (checkbox.isSelected()) {
                checkbox.click();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPackageSelected(int packageId) {
        try {
            var checkbox = this.tableUnitLoads.findElement(By.xpath(".//tbody/tr[td[2][text()='" + packageId + "']]/td[1]/input"));
            return checkbox.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public Long getShipmentId() {
        return Long.parseLong(this.spanShipmentId.getText());
    }
    
    public ShipmentListPage saveChanges() {
        this.buttonSave.click();
        return new ShipmentListPage(this.driver);
    }
    
    public ShipmentListPage cancelChanges() {
        this.buttonCancel.click();
        return new ShipmentListPage(this.driver);
    }

    public void setFromLocation(String v) { inputFrom.clear(); inputFrom.sendKeys(v); }
    public void setToLocation(String v) { inputTo.clear(); inputTo.sendKeys(v); }

    public void selectTrackableCheckboxById(Long trackableId, boolean selected) {
        WebElement cb = driver.findElement(By.xpath(
                "//input[@type='checkbox' and (@name='unitLoadIds' or @name='packageIds') and @value='" + trackableId + "']"
        ));
        if (cb.isSelected() != selected) cb.click();
    }

    public void unselectAllTrackables() {
        driver.findElements(By.xpath("//input[@type='checkbox' and (@name='unitLoadIds' or @name='packageIds')]"))
                .forEach(cb -> { if (cb.isSelected()) cb.click(); });
    }

    public ShipmentListPage submit() {
        buttonSave.click();
        return new ShipmentListPage(driver);
    }

    public boolean hasError() {
        try {
            return pError != null && pError.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getError() {
        try {
            return pError.getText();
        } catch (Exception e) {
            return "";
        }
    }

}