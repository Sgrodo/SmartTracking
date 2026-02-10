package it.univr.track.web.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TrackablesDevicesPage extends BasePageObject {

    @FindBy(xpath = "//a[@href='/web/shipments/list']")
    public WebElement linkBackShipments;

    @FindBy(xpath = "//a[@href='/web/shipments/1']")
    public WebElement linkShipmentDetails;

    @FindBy(xpath = "//h1")
    public WebElement h1TrackablesDevices;

    @FindBy(id = "error")
    public WebElement pError;

    @FindBy(xpath = "//small")
    public WebElement smallShipmentSample;

    @FindBy(xpath = "//div[contains(@id, 'trackable-')]")
    public List<WebElement> trackableSections;

    public TrackablesDevicesPage(WebDriver driver) {
        super(driver);
    }

    public class TrackableSection {
        public WebElement h3Title;
        public WebElement tableDevices;
        public WebElement selectDevice;
        public WebElement buttonBindDevice;

        public TrackableSection(WebElement section) {
            this.h3Title = section.findElement(By.xpath(".//h3"));
            this.tableDevices = section.findElement(By.xpath(".//table"));
            this.selectDevice = section.findElement(By.xpath(".//form//select"));
            this.buttonBindDevice = section.findElement(By.xpath(".//form//button"));
        }

        public record DeviceRow(String id, String origin, boolean connected, boolean calibrated, boolean state,
                                double price, WebElement unbindButton) {
        }

        public List<DeviceRow> getBoundDevices()  {
            return this.tableDevices.findElements(By.xpath(".//tbody/tr")).stream()
                    .filter(tr -> tr.findElements(By.tagName("td")).size() == 7)
                    .map(tr -> {
                        var columns = tr.findElements(By.tagName("td"));
                        var id = columns.get(0).getText();
                        var origin = columns.get(1).getText();
                        var connected = Boolean.parseBoolean(columns.get(2).getText());
                        var calibrated = Boolean.parseBoolean(columns.get(3).getText());
                        var state = Boolean.parseBoolean(columns.get(4).getText());
                        var price = Double.parseDouble(columns.get(5).getText());
                        WebElement unbindButton = columns.get(6).findElement(By.xpath(".//button[text()='Unbind']"));
                        return new DeviceRow(id, origin, connected, calibrated, state, price, unbindButton);
                    })
                    .toList();
        }

        public void bindDevice(String deviceId) {
            this.selectDevice.findElement(By.xpath(".//option[@value='" + deviceId + "']")).click();
            this.buttonBindDevice.click();
        }

        public boolean containsBoundDeviceId(String deviceId) {
            try {
                this.tableDevices.findElement(By.xpath(".//tbody/tr[td[1][text()='" + deviceId + "']]"));
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public List<Long> getAvailableDeviceIds() {
            return this.selectDevice.findElements(By.xpath(".//option[not(@value='')]")).stream()
                    .map(option -> Long.parseLong(option.getAttribute("value")))
                    .toList();
        }

    }

    public List<Long> getDisplayedTrackableIds() {
        return this.trackableSections.stream()
                .map(section -> section.findElement(By.xpath(".//h3")).getText())
                .filter(text -> text.startsWith("Trackable "))
                .map(text -> Long.parseLong(text.substring("Trackable ".length())))
                .toList();
    }

    public TrackableSection getTrackableSectionById(Long trackableId) {
        return this.trackableSections.stream()
                .filter(section -> section.findElement(By.xpath(".//h3")).getText().equals("Trackable " + trackableId))
                .findFirst()
                .map(TrackableSection::new)
                .orElse(null);
    }

    public ShipmentListPage clickBackToShipments() {
        this.linkBackShipments.click();
        return new ShipmentListPage(this.driver);
    }

    public ShipmentDetailsPage clickShipmentDetails() {
        this.linkShipmentDetails.click();
        return new ShipmentDetailsPage(this.driver);
    }

}