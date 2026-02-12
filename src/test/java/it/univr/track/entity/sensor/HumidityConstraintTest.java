package it.univr.track.entity.sensor;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HumidityConstraintTest {

    @Test
    void shouldCreateConstraint_whenPercentIsWithinRange() {
        HumidityConstraint hc = new HumidityConstraint(55.5);

        assertEquals(55.5, hc.percent());
        assertEquals(PhysicalConstraintType.HUMIDITY, hc.type());
    }

    @Test
    void shouldAllowZeroPercent() {
        HumidityConstraint hc = new HumidityConstraint(0.0);

        assertEquals(0.0, hc.percent());
    }

    @Test
    void shouldAllowHundredPercent() {
        HumidityConstraint hc = new HumidityConstraint(100.0);

        assertEquals(100.0, hc.percent());
    }


    @Test
    void shouldThrow_whenPercentIsNegative() {
        InvalidDataException ex = assertThrows(
                InvalidDataException.class,
                () -> new HumidityConstraint(-0.1)
        );

        assertEquals("Humidity must be 0..100%", ex.getMessage());
    }


    @Test
    void shouldThrow_whenPercentGreaterThan100() {
        InvalidDataException ex = assertThrows(
                InvalidDataException.class,
                () -> new HumidityConstraint(100.1)
        );

        assertEquals("Humidity must be 0..100%", ex.getMessage());
    }

    @Test
    void shouldThrowNullPointerException_whenPercentIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new HumidityConstraint(null)
        );
    }
}
