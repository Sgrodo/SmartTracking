package it.univr.track.entity;

import it.univr.track.entity.data.Sending;
import it.univr.track.entity.enumeration.TemperatureUnit;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.entity.sensor.TemperatureConstraint;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentTest {

    @Test
    void constructor_throws_whenStartDateNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Shipment("desc", null, null)
        );
    }

    @Test
    void constructor_convertsPhysicalConstraints_toEmbeddables() {
        ZonedDateTime start = ZonedDateTime.parse("2026-01-05T10:00:00+01:00[Europe/Rome]");

        Set<PhysicalConstraint> constraints = Set.of(
                new HumidityConstraint(60.0),
                new TemperatureConstraint(4.0, TemperatureUnit.CELSIUS)
        );

        Shipment s = new Shipment("food", start, constraints);

        assertEquals(start, s.getStartDate());
        assertEquals("food", s.getDescription());
        assertEquals(2, s.getPhysicalConstraints().size());

        // Verifica tramite equality "a valore" dell'embeddable
        assertTrue(s.getPhysicalConstraints().contains(
                PhysicalConstraintEmbeddable.fromDomain(new HumidityConstraint(60.0))
        ));
        assertTrue(s.getPhysicalConstraints().contains(
                PhysicalConstraintEmbeddable.fromDomain(new TemperatureConstraint(4.0, TemperatureUnit.CELSIUS))
        ));
    }

    @Test
    void addDevice_setsBidirectionalAssociation() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        Device d = new Device(true, true, true, java.math.BigDecimal.ONE, "FORMULA");

        assertNull(d.getShipment());

        s.addDevice(d);

        assertTrue(s.getDevices().contains(d));
        assertEquals(s, d.getShipment());
    }

    @Test
    void removeDevice_unsetsShipment_onlyIfRemoved() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        Device d1 = new Device(true, true, true, java.math.BigDecimal.ONE, "FORMULA");
        Device d2 = new Device(true, true, true, java.math.BigDecimal.ONE, "FORMULA");

        s.addDevice(d1);

        assertThrows(IllegalArgumentException.class,() ->{
            s.removeDevice(null);
        });

        // rimuovo un device non presente: non deve toccare shipment di d2
        s.removeDevice(d2);
        assertNull(d2.getShipment());

        // rimuovo quello presente
        s.removeDevice(d1);
        assertFalse(s.getDevices().contains(d1));
        assertNotNull(d1.getShipment());
    }

    @Test
    void addDevice_throws_whenNull() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        assertThrows(IllegalArgumentException.class, () -> s.addDevice(null));
    }

    @Test
    void removeDevice_throws_whenNull() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        assertThrows(IllegalArgumentException.class, () -> s.removeDevice(null));
    }

    @Test
    void addPhysicalConstraint_throws_whenNull(){
        Shipment s = new Shipment();
        assertThrows(IllegalArgumentException.class, () ->{
           s.addPhysicalConstraint(null);
        });
    }

    @Test
    void removePhysicalConstraint_throws_whenNull(){
        Shipment s = new Shipment();
        assertThrows(IllegalArgumentException.class, () ->{
            s.removePhysicalConstraint(null);
        });
    }

    @Test
    void addPhysicalConstraint_addsEmbeddable() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);

        PhysicalConstraint c = new HumidityConstraint(55.0);
        s.addPhysicalConstraint(c);

        assertEquals(1, s.getPhysicalConstraints().size());
        assertTrue(s.getPhysicalConstraints().contains(PhysicalConstraintEmbeddable.fromDomain(c)));
    }

    @Test
    void removePhysicalConstraint_removesAndReturnsTrue_ifPresent() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        PhysicalConstraint c = new HumidityConstraint(55.0);
        s.addPhysicalConstraint(c);

        boolean removed = s.removePhysicalConstraint(c);

        assertTrue(removed);
        assertTrue(s.getPhysicalConstraints().isEmpty());
    }

    @Test
    void removePhysicalConstraint_returnsFalse_ifNotPresent() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        boolean removed = s.removePhysicalConstraint(new HumidityConstraint(10.0));
        assertFalse(removed);
    }

    @Test
    void editPhysicalConstraint_replacesOldWithNew() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        PhysicalConstraint oldC = new HumidityConstraint(50.0);
        PhysicalConstraint newC = new HumidityConstraint(70.0);

        s.addPhysicalConstraint(oldC);
        s.editPhysicalConstraint(oldC, newC);

        assertFalse(s.getPhysicalConstraints().contains(PhysicalConstraintEmbeddable.fromDomain(oldC)));
        assertTrue(s.getPhysicalConstraints().contains(PhysicalConstraintEmbeddable.fromDomain(newC)));
    }

    @Test
    void editPhysicalConstraint_throws_ifOldNotFound() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        PhysicalConstraint oldC = new HumidityConstraint(50.0);
        PhysicalConstraint newC = new HumidityConstraint(70.0);

        assertThrows(IllegalArgumentException.class, () -> s.editPhysicalConstraint(oldC, newC));
    }

    @Test
    void editPhysicalConstraint_throws_whenArgsNull() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        PhysicalConstraint c = new HumidityConstraint(10.0);

        assertThrows(IllegalArgumentException.class, () -> s.editPhysicalConstraint(null, c));
        assertThrows(IllegalArgumentException.class, () -> s.editPhysicalConstraint(c, null));
    }

    @Test
    void addSending_addsToList() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        Sending sending = new Sending(1L, java.time.Instant.now(), true, true);

        s.addSending(sending);

        assertEquals(1, s.getSendings().size());
        assertSame(sending, s.getSendings().get(0));
    }

    @Test
    void addSending_throws_whenNull() {
        Shipment s = new Shipment("x", ZonedDateTime.now(), null);
        assertThrows(IllegalArgumentException.class, () -> s.addSending(null));
    }

    @Test
    void consistent_TrackingState(){
        Shipment s = new Shipment();
        s.setTrackingState(TrackingState.ACTIVE);
        assertEquals(TrackingState.ACTIVE, s.getTrackingState());
        s.setTrackingState(TrackingState.INACTIVE);
        assertEquals(TrackingState.INACTIVE,s.getTrackingState());
    }
}

