package it.univr.track.web.po;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

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

    @FindBy(xpath = "//input[@type='submit']")
    public WebElement buttonCreatePackage;

    public CreatePackagePage(WebDriver driver) {
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
        return this.h1CreatePackage.getText();
    }

    public List<CreateShipmentPage.UnitLoadRow> getUnitLoads() {
        return this.tableUnitLoads.findElements(org.openqa.selenium.By.xpath(".//tbody/tr")).stream()
                .filter(tr -> tr.findElements(org.openqa.selenium.By.tagName("td")).size() == 4)
                .map(tr -> {
                    var columns = tr.findElements(org.openqa.selenium.By.tagName("td"));
                    var selected = columns.get(0).findElement(org.openqa.selenium.By.tagName("input")).isSelected();
                    var id = Long.parseLong(columns.get(1).getText());
                    var description = columns.get(2).getText();
                    var numPhysicalConstraints = Long.parseLong(columns.get(3).getText());
                    return new CreateShipmentPage.UnitLoadRow(selected, id, description, numPhysicalConstraints);
                })
                .toList();
    }

    public boolean selectUnitLoadById(long id) {
        try {
            var checkbox = this.tableUnitLoads.findElement(org.openqa.selenium.By.xpath(".//tbody/tr[td[2][text()='" + id + "']]/td[1]/input"));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsUnitLoadId(long id) {
        try {
            this.tableUnitLoads.findElement(org.openqa.selenium.By.xpath(".//tbody/tr[td[2][text()='" + id + "']]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ShipmentListPage createPackage() {
        this.buttonCreatePackage.click();
        return new ShipmentListPage(this.driver);
    }


}