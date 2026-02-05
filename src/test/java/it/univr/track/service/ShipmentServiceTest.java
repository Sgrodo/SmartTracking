package it.univr.track.service;

import it.univr.track.BaseTest;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentServiceTest extends BaseTest {

    @Autowired
    private ShipmentService shipmentService;

    private UnitLoad utSample1;
    private UnitLoad utSample2;

    private Package pkgSample;

    @BeforeEach
    public void initShipmentDb() {
        utSample1 = new UnitLoad("banane", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
        utSample1 = unitLoadRepository.save(utSample1);
        utSample1.addLoadItem(this.loadItems[0]);
        utSample1 = unitLoadRepository.save(utSample1);

        utSample2 = new UnitLoad("mele", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)));
        utSample2 = unitLoadRepository.save(utSample2);
        utSample2.addLoadItem(this.loadItems[1]);
        utSample2 = unitLoadRepository.save(utSample2);

        pkgSample = new Package("frutta", "FRUTTA2024");
        pkgSample = packageRepository.save(pkgSample);
        pkgSample.addUnitLoad(utSample1);
        pkgSample.addUnitLoad(utSample2);
        pkgSample = packageRepository.save(pkgSample);
    }


    @Test
    public void testCreateShipment() {
        // Test creating a shipment with null trackables
        assertThrows(InvalidDataException.class, () -> {
            shipmentService.create(null, "Test Shipment", null);
        });

        assertThrows(InvalidDataException.class, () -> {
            shipmentService.create(null, "Test Shipment", new HashSet<>());
        });

        Shipment shipment = shipmentService.create(null, "Test Shipment", Set.of(utSample1, utSample2));

    }

    @Transactional
    @Test
    public void testPersistShipment() {
        Shipment shipment = shipmentService.create(ZonedDateTime.now(), "Test Shipment", Set.of(utSample1, utSample2));
        assertNotNull(shipment.getId());

        Shipment fetchedShipment = shipmentService.getById(shipment.getId());
        assertEquals(shipment.getId(), fetchedShipment.getId());
        assertEquals(2, fetchedShipment.getTrackables().size());

        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.getById(null);
        });

        assertThrows(NotFoundException.class, () -> {
            shipmentService.getById(7777L);
        });

        assertEquals(1, shipmentService.getAllShipments().size());

    }
}
