package it.univr.track.bdd;

import it.univr.track.entity.data.Shipment;
import it.univr.track.web.po.*;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
public class TestWorld {
    public final String baseUrl = "http://localhost:8080/web";
    public WebDriver driver;

    public CreatePackagePage createPackagePage;
    public CreateShipmentPage createShipmentPage;
    public CreateUnitLoadPage createUnitLoadPage;
    public EditShipmentPage editShipmentPage;
    public ShipmentDetailsPage shipmentDetailsPage;
    public ShipmentListPage shipmentListPage;
    public TrackablesDevicesPage trackablesDevicesPage;
    public Shipment lastShipment;

}
