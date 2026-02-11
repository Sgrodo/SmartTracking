package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.time.ZonedDateTime;

public class ShipmentDetailsPage extends BasePageObject {
    @FindBy(xpath = "//a")
    public WebElement linkBackShipments;

    @FindBy(xpath = "//h1")
    public WebElement h1ShipmentDetails;

    @FindBy(id = "error")
    public WebElement pError;

    @FindBy(id = "shipment-id")
    public WebElement spanId;

    @FindBy(id = "shipment-start-date")
    public WebElement spanStartDate;

    @FindBy(id = "shipment-end-date")
    public WebElement spanEndDate;

    @FindBy(id = "shipment-description")
    public WebElement spanDescription;

    @FindBy(id = "shipment-status")
    public WebElement spanStatus;

    @FindBy(id = "shipment-tracking-state")
    public WebElement spanTrackingState;

    @FindBy(id = "activate-btn")
    public WebElement buttonActivate;

    @FindBy(id = "deactivate-btn")
    public WebElement buttonDeactivate;

    @FindBy(id = "trackable-table")
    public WebElement tableTrackables;

    @FindBy(id = "no-trk-msg")
    public WebElement pNoTrackables;

    public ShipmentDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ShipmentListPage toShipmentListPage() {
        this.linkBackShipments.click();
        return new ShipmentListPage(this.driver);
    }

    public String getTitle() {
        return this.h1ShipmentDetails.getText();
    }

    public long getId() {
        return Long.parseLong(this.spanId.getText());
    }

    public ZonedDateTime getStartDate() {
        return ZonedDateTime.parse(this.spanStartDate.getText());
    }

    public ZonedDateTime getEndDate() {
        try {
            return ZonedDateTime.parse(this.spanEndDate.getText());
        } catch (Exception e) {
            return null;
        }
    }

    public String getDescription() {
        return this.spanDescription.getText();
    }

    public ShipmentDetailsPage activateTracking(){
        this.buttonActivate.click();
        return  new ShipmentDetailsPage(this.driver);
    }

    public ShipmentDetailsPage deActivateTracking(){
        this.buttonDeactivate.click();
        return  new ShipmentDetailsPage(this.driver);
    }

    public record TrackableRow(long id, String description, String type) {
    }

    public String getStatus() {
        return this.spanStatus.getText();
    }

    public String getTrackingState() {
        try {
            // prova 1: se esiste un id (se un giorno lo metti)
            return driver.findElement(By.id("shipment-tracking-state")).getText().trim();
        } catch (Exception ignored) {}

        // fallback: cerca una riga che contiene "Tracking" e prende la cella successiva
        return driver.findElement(By.xpath(
                "//*[self::th or self::td][contains(normalize-space(.),'Tracking')]/following-sibling::*[1]"
        )).getText().trim();
    }


    public boolean isTrackableTableVisible() {
        return this.tableTrackables == null || this.tableTrackables.isDisplayed();
    }

    public Iterable<TrackableRow> getTrackables() {
        if(!this.isTrackableTableVisible()) {
            return new java.util.ArrayList<>();
        }
        return this.tableTrackables.findElements(org.openqa.selenium.By.xpath(".//tbody/tr")).stream()
                .filter(tr -> tr.findElements(org.openqa.selenium.By.tagName("td")).size() == 3)
                .map(tr -> {
                            var columns = tr.findElements(org.openqa.selenium.By.tagName("td"));
                            var id = Long.parseLong(columns.get(0).getText());
                            var description = columns.get(1).getText();
                            var type = columns.get(2).getText();
                            return new TrackableRow(id, description, type);
                        }
                )
                .toList();
    }

    public boolean containsTrackableId(Long id) {
        if (isTrackableTableVisible()){
            return false;
        }

        try {
            this.tableTrackables.findElement(org.openqa.selenium.By.xpath(".//tbody/tr[td[1][text()='" + id + "']]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}