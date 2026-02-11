package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CreatePackagePage extends BasePageObject {

    @FindBy(xpath = "//h1")
    public WebElement h1CreatePackage;

    // se la tabella non ha id, puoi usare un xpath sul testo "Select UnitLoads to include"
    @FindBy(xpath = "//input[@type='checkbox' and @name='unitLoadIds']")
    public List<WebElement> unitLoadCheckboxes;

    @FindBy(xpath = "//input[@type='submit' and @value='Create Package']")
    public WebElement buttonCreatePackage;


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


    public CreatePackagePage(WebDriver driver) {
        super(driver);
    }

    public boolean hasError(){
        return pError.isDisplayed();
    }

    public String getError(){
        return pError.getText();
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

    public void selectUnitLoad(String unitLoadId) {
        WebElement checkbox = this.driver.findElement(
                By.xpath("//input[@type='checkbox' and @name='unitLoadIds' and @value='" + unitLoadId + "']")
        );
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    public boolean containsUnitLoadId(long id) {
        try {
            this.tableUnitLoads.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[2])='" + id + "']"
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public ShipmentListPage createPackage() {
        this.buttonCreatePackage.click();
        return new ShipmentListPage(this.driver);
    }

    public void setDescription(String description) {
        this.inputDescription.clear();
        this.inputDescription.sendKeys(description);
    }

    public String getDescription() {
        return this.inputDescription.getAttribute("value");
    }

    public void submit() {
        // Se il form usa <button type="submit">Create Package</button>
        buttonCreatePackage.click();
    }


}