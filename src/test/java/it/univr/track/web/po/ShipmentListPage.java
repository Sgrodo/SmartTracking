package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.time.ZonedDateTime;
import java.util.stream.Stream;


public class ShipmentListPage extends BasePageObject {

    @FindBy(xpath = "//table[@id='active-shipments-table']")
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
                                    Stream.of(columns.get(3).getText().split(",")).map(String::trim).map(Long::parseLong).toArray(Long[]::new);
                            var endDate = columns.get(4).getText().isBlank() ? null : ZonedDateTime.parse(columns.get(4).getText());

                            var actions = columns.get(5).findElements(By.xpath(".//button"));
                            WebElement detailsButton = actions.stream().filter(b -> b.getText().equals("Details")).findFirst().orElse(null);
                            WebElement trkAndDevicesButton = actions.stream().filter(b -> b.getText().equals("Trackables and Devices")).findFirst().orElse(null);
                            WebElement editButton = actions.stream().filter(b -> b.getText().equals("Edit ")).findFirst().orElse(null);
                            WebElement completeButton = actions.stream().filter(b -> b.getText().equals("Complete")).findFirst().orElse(null);
                            WebElement cancelButton = actions.stream().filter(b -> b.getText().equals("Cancel")).findFirst().orElse(null);

                            var state = columns.get(6).getText();
                            return new ShipmentRow(driver, id, startDate, description, trackableIds, endDate, state, detailsButton, trkAndDevicesButton, editButton, completeButton, cancelButton);
                        }
                )
                .toList();
    }

    public Iterable<ShipmentRow> getActiveShipments() {
        return getShipmentsRows(this.tableActiveShipments);
    }

    public Iterable<ShipmentRow> getFinalizedShipments() {
        return getShipmentsRows(this.tableFinalizedShipments);
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

    public record ShipmentRow(WebDriver driver, Long id, ZonedDateTime startDate, String description, Long[] trackableIds,
                              ZonedDateTime endDate, String state, WebElement detailsButton,
                              WebElement trkAndDevicesButton, WebElement editButton, WebElement completeButton,
                              WebElement cancelButton) {

        //TODO: implement methods to click buttons and return the corresponding page objects
        public ShipmentDetailsPage clickDetails() {
            this.detailsButton.click();
            return new ShipmentDetailsPage(this.driver);
        }

        public TrackablesDevicesPage clickTrackablesAndDevices() {
            this.trkAndDevicesButton.click();
            return new TrackablesDevicesPage(this.driver);
        }

        public EditShipmentPage clickEdit() {
            this.editButton.click();
            return new EditShipmentPage(this.driver);
        }

        public ShipmentListPage clickComplete() {
            this.completeButton.click();
            return new ShipmentListPage(this.driver);
        }

        public ShipmentListPage clickCancel() {
            this.cancelButton.click();
            return new ShipmentListPage(this.driver);
        }
    }
}