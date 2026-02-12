package it.univr.track.web.po;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TrackablesDevicesPage extends BasePageObject {

    @FindBy(xpath = "//a[@href='/web/shipments/list']")
    public WebElement linkBackShipments;

    @FindBy(xpath = "//h1")
    public WebElement h1TrackablesDevices;

    @FindBy(id = "error")
    public WebElement pError;

    @FindBy(xpath = "//div[contains(@id, 'trackable-')]")
    public List<WebElement> trackableSections;

    public TrackablesDevicesPage(WebDriver driver) {
        super(driver);
    }

    public class TrackableSection {
        public Long trackableId;
        public WebElement h3Title;
        public WebElement tableDevices;
        public WebElement selectDevice;
        public WebElement buttonBindDevice;
        public List<WebElement> subSectionsContainers;
        public boolean isPackage;

        public TrackableSection(WebElement section) {
            this.trackableId = Long.parseLong(section.getAttribute("id").replaceAll("\\D+", ""));
            this.h3Title = section.findElement(By.xpath(".//h3"));
            this.tableDevices = section.findElement(By.xpath(".//table"));
            this.selectDevice = section.findElement(By.xpath(".//form//select"));
            this.buttonBindDevice = section.findElement(By.xpath(".//form//button[normalize-space()='Bind device']"));
            this.subSectionsContainers = section.findElements(By.xpath(".//div[contains(@id, 'trackable-')]"));
            this.isPackage = this.h3Title.getText().toLowerCase().trim().contains("package");
        }

        public record DeviceRow(
                String id,
                String origin,
                String connected,
                String calibrated,
                String state,
                String price,
                WebElement unbindButton
        ) {}

        /**
         * Ritorna solo le righe “bound” vere (quelle con 7 <td>).
         * La riga “Nessun device assegnato” ha 1 td (colspan) e viene ignorata.
         */
        public List<DeviceRow> getBoundDevices() {
            return this.tableDevices.findElements(By.xpath(".//tbody/tr")).stream()
                    .filter(tr -> tr.findElements(By.tagName("td")).size() == 7)
                    .map(tr -> {
                        var tds = tr.findElements(By.tagName("td"));
                        WebElement unbindButton = tds.get(6).findElement(By.xpath(".//button[normalize-space()='Unbind']"));
                        return new DeviceRow(
                                tds.get(0).getText().trim(), // device id
                                tds.get(1).getText().trim(), // origin
                                tds.get(2).getText().trim(),
                                tds.get(3).getText().trim(),
                                tds.get(4).getText().trim(),
                                tds.get(5).getText().trim(),
                                unbindButton
                        );
                    })
                    .toList();
        }

        public List<TrackableSection> getSubSections() {
            return this.subSectionsContainers.stream().map(TrackableSection::new).toList();
        }

        public boolean hasAnyBoundDevice() {
            return !getBoundDevices().isEmpty();
        }

        public boolean hasBoundDeviceWithOrigin(String origin) {
            return getBoundDevices().stream().anyMatch(d -> d.origin().equals(origin));
        }

        public TrackablesDevicesPage unbindDeviceByOrigin(String origin) {
            DeviceRow row = getBoundDevices().stream()
                    .filter(d -> d.origin().equals(origin))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Bound device not found with origin: " + origin));

            row.unbindButton().click();
            return new TrackablesDevicesPage(driver);
        }

        /**
         * Cerca un device “disponibile” per origin nella select (testo option tipo: "id - origin - price").
         * Non usa repository.
         */
        public boolean isDeviceOriginAvailable(String origin) {
            return this.selectDevice.findElements(By.xpath(".//option[not(@value='') and not(@disabled)]")).stream()
                    .map(opt -> opt.getText() == null ? "" : opt.getText())
                    .anyMatch(text -> text.contains(" - " + origin + " - ") || text.contains(origin));
        }

        /**
         * Seleziona il device nella select tramite origin, poi submit.
         */
        public TrackablesDevicesPage bindDeviceByOrigin(String origin) {
            WebElement option = this.selectDevice.findElements(By.xpath(".//option[not(@value='') and not(@disabled)]")).stream()
                    .filter(opt -> {
                        String text = opt.getText() == null ? "" : opt.getText();
                        return text.contains(" - " + origin + " - ") || text.contains(origin);
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Device not available in dropdown by origin: " + origin));

            option.click();
            this.buttonBindDevice.click();
            return new TrackablesDevicesPage(driver);
        }

        public Long getTrackableId() {
            return this.trackableId;
        }
    }

    /**
     * Trova la section guardando il titolo "Trackable <id>" (come nell'HTML).
     */
    public TrackableSection getTrackableSectionById(Long trackableId) {
        return this.trackableSections.stream()
                .map(TrackableSection::new)
                .filter(s -> s.getTrackableId().equals(trackableId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Trackable section not found with id: " + trackableId));
    }

    /**
     * True se almeno una delle section mostra il device (origin) nella dropdown.
     */
    public boolean isDeviceOriginAvailableSomewhere(String origin) {
        return this.trackableSections.stream()
                .map(TrackableSection::new)
                .anyMatch(section -> section.isDeviceOriginAvailable(origin));
    }

    public ShipmentListPage clickBackToShipments() {
        this.linkBackShipments.click();
        return new ShipmentListPage(this.driver);
    }
}
