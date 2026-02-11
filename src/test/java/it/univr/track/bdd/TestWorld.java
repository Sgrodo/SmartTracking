package it.univr.track.bdd;

import it.univr.track.entity.data.Shipment;
import it.univr.track.web.po.*;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestWorld {
    public final String baseUrl = "http://localhost:8080/web";
    public WebDriver driver;

    //page objects
    public CreatePackagePage createPackagePage;
    public CreateShipmentPage createShipmentPage;
    public CreateUnitLoadPage createUnitLoadPage;
    public EditShipmentPage editShipmentPage;
    public ShipmentDetailsPage shipmentDetailsPage;
    public ShipmentListPage shipmentListPage;
    public TrackablesDevicesPage trackablesDevicesPage;

    // === scenario context ===
    public final Map<String, Long> shipmentIdByDescription = new HashMap<>();
    public final Map<String, Long> trackableIdByDescription = new HashMap<>();
    public final Map<String, Long> deviceIdByOrigin = new HashMap<>();

    public Shipment lastShipment;


    public Long requireShipmentId(String shipmentDescription) {
        Long id = shipmentIdByDescription.get(shipmentDescription);
        if (id == null) throw new AssertionError("Missing cached shipment id for description: " + shipmentDescription);
        return id;
    }

    public Long requireTrackableId(String trackableDescription) {
        Long id = trackableIdByDescription.get(trackableDescription);
        if (id == null) throw new AssertionError("Missing cached trackable id for description: " + trackableDescription);
        return id;
    }

    public Long requireDeviceId(String origin) {
        Long id = deviceIdByOrigin.get(origin);
        if (id == null) throw new AssertionError("Missing cached device id for origin: " + origin);
        return id;
    }


}
