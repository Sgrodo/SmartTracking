package it.univr.track.entity;

import it.univr.track.entity.enumeration.TrackingState;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DeviceTest {
    /*
    @Test
    void constructor_setsFieldsCorrectly() {
        Device d = new Device(
                true,
                true,
                false,
                new BigDecimal("12.50"),
                "FORMULA_X"
        );

        assertTrue(d.getIsConnected());
        assertTrue(d.getState());
        assertFalse(d.getIsCalibrated());
        assertEquals(new BigDecimal("12.50"), d.getPrice());
        assertEquals("FORMULA_X", d.getOrigin());
        assertNull(d.getUnitLoad()); // non settata dal costruttore
    }

    @Test
    void constructor_throws_whenPriceIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Device(true, true, true, null, "X")
        );
    }

    @Test
    void constructor_throws_whenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new Device(true, true, true, new BigDecimal("-0.01"), "X")
        );
    }

    @Test
    void constructor_throws_whenOriginIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Device(true, true, true, new BigDecimal("1.00"), null)
        );
    }

    @Test
    void constructor_throws_whenOriginIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new Device(true, true, true, new BigDecimal("1.00"), "   ")
        );
    }

    @Test
    void setters_work() {
        Device d = new Device();
        d.setIsConnected(false);
        d.setState(true);
        d.setIsCalibrated(true);
        d.setPrice(new BigDecimal("99.99"));
        d.setOrigin("FORMULA_Y");

        assertFalse(d.getIsConnected());
        assertTrue(d.getState());
        assertTrue(d.getIsCalibrated());
        assertEquals(new BigDecimal("99.99"), d.getPrice());
        assertEquals("FORMULA_Y", d.getOrigin());
    }

    @Test
    void consistentTrackingState(){
        Device d = new Device();
        d.setTrackingState(TrackingState.ACTIVE);
        assertEquals(TrackingState.ACTIVE, d.getTrackingState());
        d.setTrackingState(TrackingState.INACTIVE);
        assertEquals(TrackingState.INACTIVE, d.getTrackingState());
    }*/
}
