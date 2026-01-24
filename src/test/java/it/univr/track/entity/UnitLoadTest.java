package it.univr.track.entity;

import it.univr.track.entity.data.Sending;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.TemperatureUnit;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.entity.sensor.TemperatureConstraint;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UnitLoadTest {

    @Test
    void constructor_throws_whenStartDateNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new UnitLoad("desc", null, null)
        );
    }

    @Test
    void constructor_convertsPhysicalConstraints_toEmbeddables() {
        ZonedDateTime start = ZonedDateTime.parse("2026-01-05T10:00:00+01:00[Europe/Rome]");

        Set<PhysicalConstraint> constraints = Set.of(
                new HumidityConstraint(60.0),
                new TemperatureConstraint(4.0, TemperatureUnit.CELSIUS)
        );

        UnitLoad s = new UnitLoad("food", start, constraints);

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
    void addPhysicalConstraint_throws_whenNull(){
        UnitLoad s = new UnitLoad();
        assertThrows(IllegalArgumentException.class, () ->{
           s.addPhysicalConstraint(null);
        });
    }

    @Test
    void removePhysicalConstraint_throws_whenNull(){
        UnitLoad s = new UnitLoad();
        assertThrows(IllegalArgumentException.class, () ->{
            s.removePhysicalConstraint(null);
        });
    }

    @Test
    void addPhysicalConstraint_addsEmbeddable() {
        UnitLoad s = new UnitLoad("x", ZonedDateTime.now(), null);

        PhysicalConstraint c = new HumidityConstraint(55.0);
        s.addPhysicalConstraint(c);

        assertEquals(1, s.getPhysicalConstraints().size());
        assertTrue(s.getPhysicalConstraints().contains(PhysicalConstraintEmbeddable.fromDomain(c)));
    }

    @Test
    void removePhysicalConstraint_removesAndReturnsTrue_ifPresent() {
        UnitLoad s = new UnitLoad("x", ZonedDateTime.now(), null);
        PhysicalConstraint c = new HumidityConstraint(55.0);
        s.addPhysicalConstraint(c);

        boolean removed = s.removePhysicalConstraint(c);

        assertTrue(removed);
        assertTrue(s.getPhysicalConstraints().isEmpty());
    }

    @Test
    void removePhysicalConstraint_returnsFalse_ifNotPresent() {
        UnitLoad s = new UnitLoad("x", ZonedDateTime.now(), null);
        boolean removed = s.removePhysicalConstraint(new HumidityConstraint(10.0));
        assertFalse(removed);
    }

    @Test
    void editPhysicalConstraint_replacesOldWithNew() {
        UnitLoad s = new UnitLoad("x", ZonedDateTime.now(), null);
        PhysicalConstraint oldC = new HumidityConstraint(50.0);
        PhysicalConstraint newC = new HumidityConstraint(70.0);

        s.addPhysicalConstraint(oldC);
        s.editPhysicalConstraint(oldC, newC);

        assertFalse(s.getPhysicalConstraints().contains(PhysicalConstraintEmbeddable.fromDomain(oldC)));
        assertTrue(s.getPhysicalConstraints().contains(PhysicalConstraintEmbeddable.fromDomain(newC)));
    }

    @Test
    void editPhysicalConstraint_throws_ifOldNotFound() {
        UnitLoad s = new UnitLoad("x", ZonedDateTime.now(), null);
        PhysicalConstraint oldC = new HumidityConstraint(50.0);
        PhysicalConstraint newC = new HumidityConstraint(70.0);

        assertThrows(IllegalArgumentException.class, () -> s.editPhysicalConstraint(oldC, newC));
    }

    @Test
    void editPhysicalConstraint_throws_whenArgsNull() {
        UnitLoad s = new UnitLoad("x", ZonedDateTime.now(), null);
        PhysicalConstraint c = new HumidityConstraint(10.0);

        assertThrows(IllegalArgumentException.class, () -> s.editPhysicalConstraint(null, c));
        assertThrows(IllegalArgumentException.class, () -> s.editPhysicalConstraint(c, null));
    }



    @Test
    void consistent_TrackingState(){
        UnitLoad s = new UnitLoad();
        s.setTrackingState(TrackingState.ACTIVE);
        assertEquals(TrackingState.ACTIVE, s.getTrackingState());
        s.setTrackingState(TrackingState.INACTIVE);
        assertEquals(TrackingState.INACTIVE,s.getTrackingState());
    }
}

