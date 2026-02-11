package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

public class ShipmentListPage extends BasePageObject {

    @FindBy(id = "active-shipments-table")
    public WebElement tableActiveShipments;

    @FindBy(xpath = "//table[@id='finalized-shipments-table']")
    public WebElement tableFinalizedShipments;

    @FindBy(xpath = "/html/body/p/a/button")
    public WebElement buttonCreateNewShipment;

    public ShipmentListPage(WebDriver driver) {
        super(driver);
    }

    protected Iterable<ShipmentRow> getShipmentsRows(WebElement tableElement) {
        return tableElement.findElements(By.xpath(".//tbody/tr")).stream()
                .filter(tr -> tr.findElements(By.tagName("td")).size() == 7)
                .map(tr -> {
                    var columns = tr.findElements(By.tagName("td"));
                    var id = Long.parseLong(columns.get(0).getText());
                    var startDate = ZonedDateTime.parse(columns.get(1).getText());
                    var description = columns.get(2).getText();
                    var trackableIds = columns.get(3).getText().isBlank() ? new Long[0] :
                            Stream.of(columns.get(3).getText().split(","))
                                    .map(String::trim)
                                    .map(Long::parseLong)
                                    .toArray(Long[]::new);

                    var endText = columns.get(4).getText().trim();
                    var endDate = (endText.isBlank() || endText.equals("-")) ? null : ZonedDateTime.parse(endText);

                    var actions = columns.get(5).findElements(By.xpath(".//button"));
                    WebElement detailsButton = actions.stream()
                            .filter(b -> "Details".equals(b.getText()))
                            .findFirst().orElse(null);

                    WebElement trkAndDevicesButton = actions.stream()
                            .filter(b -> b.getText() != null && b.getText().trim().contains("Trackables"))
                            .findFirst().orElse(null);

                    WebElement editButton = actions.stream()
                            // nel tuo html è "Edit", nel tuo codice c'era "Edit " (con spazio): lo lascio robusto
                            .filter(b -> b.getText() != null && b.getText().trim().equals("Edit"))
                            .findFirst().orElse(null);

                    WebElement completeButton = actions.stream()
                            .filter(b -> "Complete".equals(b.getText()))
                            .findFirst().orElse(null);

                    WebElement cancelButton = actions.stream()
                            .filter(b -> "Cancel".equals(b.getText()))
                            .findFirst().orElse(null);

                    var state = columns.get(6).getText();

                    return new ShipmentRow(
                            driver, id, startDate, description, trackableIds, endDate, state,
                            detailsButton, trkAndDevicesButton, editButton, completeButton, cancelButton
                    );
                })
                .toList();
    }

    public Iterable<ShipmentRow> getActiveShipments() {
        return getShipmentsRows(this.tableActiveShipments);
    }

    public Iterable<ShipmentRow> getFinalizedShipments() {
        return getShipmentsRows(this.tableFinalizedShipments);
    }

    public ShipmentRow getActiveShipmentByDescription(String description) {
        for (ShipmentRow row : getActiveShipments()) {
            if (description.equals(row.description())) {
                return row;
            }
        }
        throw new AssertionError("Active shipment not found: " + description);
    }

    protected boolean containsShipmentId(WebElement tableElement, Long id) {
        try {
            tableElement.findElement(By.xpath(".//tbody/tr[td[1][text()='" + id + "']]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsActiveShipmentId(Long id) {
        return containsShipmentId(this.tableActiveShipments, id);
    }

    public boolean containsFinalizedShipmentId(Long id) {
        return containsShipmentId(this.tableFinalizedShipments, id);
    }

    public CreateShipmentPage clickCreateNewShipment() {
        this.buttonCreateNewShipment.click();
        return new CreateShipmentPage(this.driver);
    }

    public record ShipmentRow(
            WebDriver driver,
            Long id,
            ZonedDateTime startDate,
            String description,
            Long[] trackableIds,
            ZonedDateTime endDate,
            String state,
            WebElement detailsButton,
            WebElement trkAndDevicesButton,
            WebElement editButton,
            WebElement completeButton,
            WebElement cancelButton
    ) {
        public ShipmentDetailsPage clickDetails() {
            if (this.detailsButton == null) throw new AssertionError("Details button not found for shipment row " + id);
            this.detailsButton.click();
            return new ShipmentDetailsPage(this.driver);
        }

        public TrackablesDevicesPage clickTrackablesAndDevices() {
            if (this.trkAndDevicesButton == null) {
                throw new AssertionError("Trackables & Devices button not found for shipment row " + id);
            }
            this.trkAndDevicesButton.click();
            return new TrackablesDevicesPage(this.driver);
        }

        public EditShipmentPage clickEdit() {
            if (this.editButton == null) throw new AssertionError("Edit button not found for shipment row " + id);
            this.editButton.click();
            return new EditShipmentPage(this.driver);
        }

        public ShipmentListPage clickComplete() {
            if (this.completeButton == null) throw new AssertionError("Complete button not found for shipment row " + id);
            this.completeButton.click();
            return new ShipmentListPage(this.driver);
        }

        public ShipmentListPage clickCancel() {
            if (this.cancelButton == null) throw new AssertionError("Cancel button not found for shipment row " + id);
            this.cancelButton.click();
            return new ShipmentListPage(this.driver);
        }
    }
}
