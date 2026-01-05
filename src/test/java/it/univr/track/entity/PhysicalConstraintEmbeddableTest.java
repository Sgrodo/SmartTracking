package it.univr.track.entity;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.entity.enumeration.TemperatureUnit;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.entity.sensor.TemperatureConstraint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicalConstraintEmbeddableTest {

    @Test
    void fromDomain_createsEmbeddable_fromHumidityConstraint() {
        PhysicalConstraint c = new HumidityConstraint(55.0);

        PhysicalConstraintEmbeddable e = PhysicalConstraintEmbeddable.fromDomain(c);

        // non posso leggere i campi (sono private) quindi verifico con round-trip
        PhysicalConstraint back = e.toDomain();

        assertTrue(back instanceof HumidityConstraint);
        assertEquals(PhysicalConstraintType.HUMIDITY, back.type());
        assertEquals(55.0, ((HumidityConstraint) back).percent());
    }

    @Test
    void fromDomain_createsEmbeddable_fromTemperatureConstraint() {
        PhysicalConstraint c = new TemperatureConstraint(4.5, TemperatureUnit.CELSIUS);

        PhysicalConstraintEmbeddable e = PhysicalConstraintEmbeddable.fromDomain(c);

        PhysicalConstraint back = e.toDomain();

        assertTrue(back instanceof TemperatureConstraint);
        assertEquals(PhysicalConstraintType.TEMPERATURE, back.type());
        TemperatureConstraint t = (TemperatureConstraint) back;
        assertEquals(4.5, t.value());
        assertEquals(TemperatureUnit.CELSIUS, t.unit());
    }

    @Test
    void equalsAndHashCode_work_asValueObject() {
        PhysicalConstraintEmbeddable a =
                PhysicalConstraintEmbeddable.fromDomain(new TemperatureConstraint(1.0, TemperatureUnit.CELSIUS));

        PhysicalConstraintEmbeddable b =
                PhysicalConstraintEmbeddable.fromDomain(new TemperatureConstraint(1.0, TemperatureUnit.CELSIUS));

        PhysicalConstraintEmbeddable c =
                PhysicalConstraintEmbeddable.fromDomain(new TemperatureConstraint(2.0, TemperatureUnit.CELSIUS));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, c);
    }

    @Test
    void humidityEmbeddable_notEqual_temperatureEmbeddable() {
        PhysicalConstraintEmbeddable h =
                PhysicalConstraintEmbeddable.fromDomain(new HumidityConstraint(50.0));

        PhysicalConstraintEmbeddable t =
                PhysicalConstraintEmbeddable.fromDomain(new TemperatureConstraint(50.0, TemperatureUnit.CELSIUS));

        assertNotEquals(h, t);
    }
}

