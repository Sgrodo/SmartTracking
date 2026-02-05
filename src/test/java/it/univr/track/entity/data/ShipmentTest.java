package it.univr.track.entity.data;

import it.univr.track.BaseTest;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.repository.PackageRepository;
import it.univr.track.repository.ShipmentRepository;
import it.univr.track.repository.UnitLoadRepository;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentTest extends BaseTest {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private UnitLoadRepository unitLoadRepository;

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

    @AfterEach
    public void resetShipmentDb() {
        shipmentRepository.deleteAll();
        unitLoadRepository.deleteAll();
    }

    @Test
    public void testAddTrackable() {
        // Test adding a trackable to a shipment
        Shipment s = new Shipment(ZonedDateTime.now(), "shipment");
        s.addTrackable(utSample1);
        s.addTrackable(utSample2);
        assertArrayEquals(List.of(utSample1, utSample2).toArray(), s.getTrackables().toArray());

        // Test removing a trackable from a shipment
        s.removeTrackable(utSample1);
        assertArrayEquals(List.of(utSample2).toArray(), s.getTrackables().toArray());
    }

    @Test
    public void testUniqueness() {
        Shipment s1 = new Shipment(ZonedDateTime.now(), "shipment");
        s1.addTrackable(utSample1);
        s1.addTrackable(utSample1);

        Set<Trackable> trackables = s1.getTrackables();
        assertEquals(1, trackables.size());
    }

    @Test
    public void testAddTrackableAssignedToAnotherShipment() {
        Shipment s1 = new Shipment(ZonedDateTime.now(), "shipment1");
        Shipment s2 = new Shipment(ZonedDateTime.now(), "shipment2");

        s1.addTrackable(utSample1);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            s2.addTrackable(utSample1);
        });

        String expectedMessage = "Trying to add trackable";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testOverlappedTrackablesInShipment() {
        Shipment shipment = new Shipment("Test Shipment with Overlapped Trackables");
        shipment.addTrackable(utSample1);
        shipment.addTrackable(pkgSample);
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            shipmentRepository.saveAndFlush(shipment);
        });
        String expectedMessage = "is assigned to multiple Trackables in the same Shipment";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testStates() {
        Shipment s = new Shipment("shipment");

        s.setState(ShipmentState.IN_TRANSIT);
        assertEquals(ShipmentState.IN_TRANSIT, s.getState());

        s.setState(ShipmentState.COMPLETED);
        assertEquals(ShipmentState.COMPLETED, s.getState());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            s.setState(ShipmentState.CANCELLED);
        });

        String expectedMessage = "Cannot change state of a final shipment";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(IllegalStateException.class, s::setInTransit);

        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage));

        s.setState(ShipmentState.CREATED);
        assertEquals(ShipmentState.CREATED, s.getState());

        //cancelliamo lo shipment
        s.cancel();
        assertEquals(ShipmentState.CANCELLED, s.getState());

        s.setState(ShipmentState.CREATED);
        s.cancel(ZonedDateTime.now());
        assertEquals(ShipmentState.CANCELLED, s.getState());

        assertThrows(IllegalStateException.class, s::complete);

    }

    @Test
    public void testFields(){
        ZonedDateTime startDate = ZonedDateTime.now();
        Shipment s = new Shipment( startDate,"Test Shipment");
        assertEquals(startDate, s.getStartDate());
        assertNull(s.getEndDate());
        assertEquals("Test Shipment", s.getDescription());


    }

    @Test
    public void persistentTests(){
        UnitLoad ul1 = unitLoadRepository.findById(utSample1.getId()).orElseThrow();
        UnitLoad ul2 = unitLoadRepository.findById(utSample2.getId()).orElseThrow();

        Shipment s = shipmentRepository.saveAndFlush(new Shipment("Persistent Shipment"));

        s.addTrackable(ul1);
        s.addTrackable(ul2);

        shipmentRepository.saveAndFlush(s); // qui fa UPDATE, non INSERT dei figli -> non mi da erore finalmente
    }

}
