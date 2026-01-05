package it.univr.track.entity.data;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SendingTest {

    @Test
    void constructor_setsFieldsCorrectly() {
        Long deviceId = 42L;
        Instant ts = Instant.parse("2026-01-05T10:15:30Z");

        Sending s = new Sending(deviceId, ts, true, false);

        assertEquals(deviceId, s.getDeviceId());
        assertEquals(ts, s.getTimestamp());
        assertTrue(s.getSecureChannel());
        assertFalse(s.getDataIntegrity());
    }

    @Test
    void setters_work() {
        Sending s = new Sending();

        Instant ts = Instant.now();
        s.setDeviceId(7L);
        s.setTimestamp(ts);
        s.setSecureChannel(false);
        s.setDataIntegrity(true);

        assertEquals(7L, s.getDeviceId());
        assertEquals(ts, s.getTimestamp());
        assertFalse(s.getSecureChannel());
        assertTrue(s.getDataIntegrity());
    }
}
