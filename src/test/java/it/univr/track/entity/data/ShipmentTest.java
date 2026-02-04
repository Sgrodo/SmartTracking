package it.univr.track.entity.data;

import it.univr.track.BaseTest;
import it.univr.track.entity.sensor.HumidityConstraint;
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
    private UnitLoadRepository unitLoadRepository;

    private UnitLoad utSample1;
    private UnitLoad utSample2;

    @BeforeEach
    public void initShipmentDb() {
        utSample1 = new UnitLoad("banane", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)), List.of(this.loadItems[0]));
        utSample2 = new UnitLoad("mele", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)), List.of(this.loadItems[1]));

        utSample1 = unitLoadRepository.save(utSample1);
        utSample2 = unitLoadRepository.save(utSample2);
    }

    @AfterEach
    public void resetShipmentDb() {
        shipmentRepository.deleteAll();
        unitLoadRepository.deleteAll();
    }

    @Test
    public void testShipmentConstraints() {
        // Test creation with null trackables
        assertThrows(IllegalArgumentException.class, () -> {
            new Shipment(null, "Invalid Shipment", null);
        });

        // Test creation with empty trackables
        assertThrows(IllegalArgumentException.class, () -> {
            new Shipment(null, "Invalid Shipment", List.of());
        });

        // Test saving a shipment without trackables
        Exception e = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            Shipment shipment = new Shipment(null, "Invalid Shipment",
                    List.of(utSample1));
            shipment.removeTrackable(utSample1);
            shipmentRepository.save(shipment);
        });
        assert e.getMessage().equals("Shipment must have at least one Trackable");
    }

    @Test
    public void testAddTrackable() {
        // Test adding a trackable to a shipment
        Shipment s = new Shipment(ZonedDateTime.now(), "shipment", List.of(utSample1));
        s.addTrackable(utSample2);
        assertArrayEquals(List.of(utSample1, utSample2).toArray(), s.getTrackables().toArray());

        // Test removing a trackable from a shipment
        s.removeTrackable(utSample1);
        assertArrayEquals(List.of(utSample2).toArray(), s.getTrackables().toArray());
    }

    @Test
    public void testUniqueness() {
        Shipment s1 = new Shipment(ZonedDateTime.now(), "shipment", List.of(utSample1));
        s1.addTrackable(utSample1);

        Set<@NotNull Trackable> trackables = s1.getTrackables();
        assertEquals(1, trackables.size());
    }


}
