package it.univr.track.service;

import it.univr.track.BaseTest;
import it.univr.track.entity.Device;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentServiceTest extends BaseTest {

    @Autowired
    private ShipmentService shipmentService;

    private UnitLoad utSample1;
    private UnitLoad utSample2;

    private Package pkgSample;
    @Autowired
    private DeviceAssignmentService deviceAssignmentService;
    @Autowired
    private UnitLoadService unitLoadService;

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

    @AfterEach
    public void cleanShipmentDb() {
        super.cleanBaseDb();
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
        HashSet<Shipment> shipments = new HashSet<>(shipmentService.getAllShipments());
        assertEquals(1, shipments.size());

    }

    @Transactional
    @Test
    public void testUpdateShipment() {
        Shipment shipment = shipmentService.create(ZonedDateTime.now(), "Test Shipment", Set.of(utSample1));
        Shipment unsavedShipment = new Shipment("Unsave Shipment");
// No more need to test
//        assertThrows(IllegalArgumentException.class, () -> {
//            shipmentService.update(shipment, "Updated Shipment", null, null, Set.of(utSample1, utSample2));
//        });

        assertThrows(InvalidDataException.class, () -> {
            shipmentService.update(shipment, "Updated Shipment", ZonedDateTime.now(), ShipmentState.IN_TRANSIT, null);
        });

        assertThrows(InvalidDataException.class, () -> {
            shipmentService.update(shipment, "Updated Shipment", ZonedDateTime.now(), ShipmentState.IN_TRANSIT, new HashSet<>());
        });

        assertThrows(InvalidDataException.class, () -> {
            shipmentService.update(null, "Updated Shipment", ZonedDateTime.now(), ShipmentState.IN_TRANSIT, Set.of(utSample1, utSample2));
        });

        assertThrows(NotFoundException.class, () -> {
            shipmentService.update(unsavedShipment, "Updated Shipment", ZonedDateTime.now(), ShipmentState.IN_TRANSIT, Set.of(utSample1, utSample2));
        });

        Shipment updatedShipment = shipmentService.getById(shipment.getId());
        assertEquals("Test Shipment", updatedShipment.getDescription());
        assertEquals(1, updatedShipment.getTrackables().size());

        ZonedDateTime newEndDate = ZonedDateTime.now();
        shipmentService.update(updatedShipment, "changed description", newEndDate, ShipmentState.IN_TRANSIT, Set.of(utSample1, utSample2));

        assertEquals("changed description", updatedShipment.getDescription());
        assertEquals(2, updatedShipment.getTrackables().size());
        assertEquals(ShipmentState.IN_TRANSIT, updatedShipment.getState());
    }

    @Transactional
    @Test
    public void testDeleteShipment() {
        Shipment shipment = shipmentService.create(ZonedDateTime.now(), "Test Shipment", Set.of(utSample1));
        Long shipmentId = shipment.getId();

        shipmentService.deleteById(shipmentId);

        assertThrows(NotFoundException.class, () -> {
            shipmentService.getById(shipmentId);
        });
    }

    @Transactional
    @Test
    public void testTrackingState() {
        Shipment shipment = shipmentService.create(ZonedDateTime.now(),
                "Test Shipment", Set.of(utSample1, utSample2));

        TrackingState expectedState = TrackingState.INACTIVE;
        Device device1 = new Device(true,true,true, new BigDecimal("1.0"),"Italy");
        Device device2 = new Device(true,true,true, new BigDecimal("2.0"),"Germany");

        device1 = deviceRepository.save(device1);
        device2 = deviceRepository.save(device2);

        deviceAssignmentService.bind(device1.getId(), utSample1.getId());
        deviceAssignmentService.bind(device2.getId(), utSample2.getId());

        assertEquals(TrackingState.INACTIVE, shipmentService.getTrackingState(shipment));

        shipmentService.setTrackingState(shipment.getId(), TrackingState.ACTIVE);
        assertEquals(TrackingState.ACTIVE, shipmentService.getTrackingState(shipment));
    }

    @Transactional
    @Test
    public void testCompleteShipment() {
        // Create shipment and verify initial state
        Shipment shipment = shipmentService.create(ZonedDateTime.now(),
                "Test Shipment", Set.of(utSample1, utSample2));

        assertEquals(ShipmentState.CREATED, shipment.getState());

        // Update shipment state to COMPLETED and verify
        shipmentService.update(shipment, shipment.getDescription(), ZonedDateTime.now(),
                ShipmentState.COMPLETED, shipment.getTrackables());

        Shipment completedShipment = shipmentService.getById(shipment.getId());

        assertEquals(ShipmentState.COMPLETED, completedShipment.getState());

        //-----------------------------------------------------------------
        //creo un device e lo associo ad un trackable dello shipment completato (invalido)
        Device device1 = new Device(true,true,true, new BigDecimal("1.0"),"Italy");
        device1 = deviceRepository.save(device1);
        final Device finalDevice = device1; // serve per usarlo dentro la lambda, altrimenti non è effettivamente final e non compila
        assertThrows(InvalidDataException.class, () ->{
            deviceAssignmentService.bind(finalDevice.getId(), utSample1.getId());
        });

        //illegale se non completi
        assertThrows(IllegalStateException.class, ()->{
            Shipment s = shipmentService.create(null,"test shipment",Set.of(utSample1,utSample2));
        });

        // Testing dell'associazione di un device prima appartenente ad uno shipment completato (dovrebbe essere possibile)
        UnitLoad unitLoad = unitLoadService.createUnitLoad(
                null, null, null, List.of(loadItems[0]));

        Shipment newShipment = shipmentService.create(null,"test shipment",Set.of(unitLoad));

        deviceAssignmentService.bind(device1.getId(), unitLoad.getId());

        assertEquals(ShipmentState.CREATED, shipmentService.getById(newShipment.getId()).getState());

        shipmentService.completeShipment(newShipment);

        assertEquals(ShipmentState.COMPLETED, shipmentService.getById(newShipment.getId()).getState());

    }

}
