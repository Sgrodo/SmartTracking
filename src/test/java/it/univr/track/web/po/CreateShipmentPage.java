package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateShipmentPage extends BasePageObject {
    @FindBy(xpath = "//a[@href='/web/shipments/list']")
    public WebElement linkBackShipments;

    @FindBy(id = "from-input")
    public WebElement fromLocation;

    @FindBy(id = "to-input")
    public WebElement toLocation;

    @FindBy(xpath = "//h1")
    public WebElement h1CreateShipment;

    @FindBy(xpath = "//p[@id='error']")
    public WebElement pError;

    @FindBy(xpath = "//a[@href='/web/unitloads/new']")
    public WebElement linkNewUnitLoad;

    @FindBy(xpath = "//a[@href='/web/packages/new']")
    public WebElement linkNewPackage;

    @FindBy(id = "description-textarea")
    public WebElement textareaDescription;

    @FindBy(xpath = "//table[@id='unit-loads-table']")
    public WebElement tableUnitLoads;

    @FindBy(xpath = "//table[@id='packages-table']")
    public WebElement tablePackages;

    @FindBy(xpath = "//input[@type='submit']")
    public WebElement inputCreateShipment;

    public CreateShipmentPage(WebDriver driver) {
        super(driver);
    }

    public boolean hasError() {
        return this.pError.isDisplayed();
    }

    public String getError() {
        return hasError() ? this.pError.getText() : null;
    }

    public void setDescription(String description) {
        this.textareaDescription.clear();
        this.textareaDescription.sendKeys(description);
    }

    public boolean selectUnitLoadByDescription(String unitLoadDescription) {
        try {
            var checkbox = tableUnitLoads.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[3])='" + unitLoadDescription + "']//td[1]//input[@type='checkbox' and @name='unitLoadIds']"
            ));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean selectPackageByDescription(String packageDescription) {
        try {
            var checkbox = tablePackages.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[3])='" + packageDescription + "']//td[1]//input[@type='checkbox' and @name='packageIds']"
            ));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsUnitLoadDescription(String unitLoadDescription) {
        try {
            tableUnitLoads.findElement(By.xpath(".//tbody/tr[normalize-space(td[3])='" + unitLoadDescription + "']"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsPackageDescription(String packageDescription) {
        try {
            tablePackages.findElement(By.xpath(".//tbody/tr[normalize-space(td[3])='" + packageDescription + "']"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ShipmentListPage toShipmentListPage() {
        this.linkBackShipments.click();
        return new ShipmentListPage(this.driver);
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation.clear();
        this.fromLocation.sendKeys(fromLocation);
    }

    public void setToLocation(String toLocation) {
        this.toLocation.clear();
        this.toLocation.sendKeys(toLocation);
    }

    public String getFromLocation() {
        return this.fromLocation.getAttribute("value");
    }

    public String getToLocation() {
        return this.toLocation.getAttribute("value");
    }


    public String getTitle() {
        return this.h1CreateShipment.getText();
    }

    public CreateUnitLoadPage toCreateUnitLoadPage() {
        this.linkNewUnitLoad.click();
        return new CreateUnitLoadPage(this.driver);
    }

    public CreatePackagePage toCreatePackagePage() {
        this.linkNewPackage.click();
        return new CreatePackagePage(this.driver);
    }

    public void insertDescription(String description) {
        this.textareaDescription.sendKeys(description);
    }

    public int getUnitLoadsCount() {
        return this.tableUnitLoads.findElements(By.xpath(".//tbody/tr")).size();
    }

    public int getPackagesCount() {
        return this.tablePackages.findElements(By.xpath(".//tbody/tr")).size();
    }

    public boolean selectUniLoad(Long unitLoadId) {
        try {
            var checkbox = tableUnitLoads.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[2])='" + unitLoadId + "']//td[1]//input[@type='checkbox' and @name='unitLoadIds']"
            ));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean selectPackage(Long packageId) {
        try {
            var checkbox = tablePackages.findElement(By.xpath(
                    ".//tbody/tr[normalize-space(td[2])='" + packageId + "']//td[1]//input[@type='checkbox' and @name='packageIds']"
            ));
            if (!checkbox.isSelected()) checkbox.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Iterable<UnitLoadRow> getUnitLoads() {
        return this.tableUnitLoads.findElements(By.xpath(".//tbody/tr")).stream()
                .filter(tr -> tr.findElements(By.tagName("td")).size() == 4)
                .map(tr -> {
                            var columns = tr.findElements(By.tagName("td"));
                            var selected = columns.get(0).findElement(By.tagName("input")).isSelected();
                            var id = Long.parseLong(columns.get(1).getText());
                            var description = columns.get(2).getText();
                            var numPhysicalConstraints = Long.parseLong(columns.get(3).getText());
                            return new UnitLoadRow(selected, id, description, numPhysicalConstraints);
                        }
                )
                .toList();
    }

    public Iterable<PackageRow> getPackages() {
        return this.tablePackages.findElements(By.xpath(".//tbody/tr")).stream()
                .filter(tr -> tr.findElements(By.tagName("td")).size() == 4)
                .map(tr -> {
                            var columns = tr.findElements(By.tagName("td"));
                            var selected = columns.get(0).findElement(By.tagName("input")).isSelected();
                            var id = Long.parseLong(columns.get(1).getText());
                            var description = columns.get(2).getText();
                            var numUnitLoads = Long.parseLong(columns.get(3).getText());
                            return new PackageRow(selected, id, description, numUnitLoads);
                        }
                )
                .toList();
    }

    public ShipmentListPage createShipment() {
        this.inputCreateShipment.click();
        return new ShipmentListPage(this.driver);
    }

    public record UnitLoadRow(boolean selected, long id, String description, long numPhysicalConstraints) {
    }

    public record PackageRow(boolean selected, long id, String description, long numUnitLoads) {
    }


}