package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.data.Sending;
import it.univr.track.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceServiceTest {

    private ShipmentRepository shipmentRepository;
    private SendingFactory sendingFactory;
    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        shipmentRepository = mock(ShipmentRepository.class);
        sendingFactory = mock(SendingFactory.class);
        deviceService = new DeviceService(shipmentRepository, sendingFactory);
    }

    // ---------- addDevice ----------

    @Test
    void addDevice_registersDeviceId() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", 10L);

        assertDoesNotThrow(() -> deviceService.addDevice(5L, d));
    }

    @Test
    void addDevice_throws_whenDeviceIdNull() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");

        assertThrows(IllegalArgumentException.class, () ->
                deviceService.addDevice(1L, d)
        );
    }

    // ---------- removeDevice ----------

    @Test
    void removeDevice_removesRegisteredDevice() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", 7L);

        deviceService.addDevice(3L, d);

        assertDoesNotThrow(() -> deviceService.removeDevice(d));
    }

    @Test
    void removeDevice_throws_whenDeviceNotRegistered() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", 99L);

        assertThrows(IllegalStateException.class, () ->
                deviceService.removeDevice(d)
        );
    }

    @Test
    void removeDevice_throws_whenDeviceIdNull() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");

        assertThrows(IllegalArgumentException.class, () ->
                deviceService.removeDevice(d)
        );
    }

    // ---------- ingestMockSending ----------

    @Test
    void ingestMockSending_createsAndAddsSending() {
        Long deviceId = 1L;
        Long shipmentId = 2L;

        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", deviceId);

        Shipment shipment = new Shipment("x", ZonedDateTime.now(), null);

        deviceService.addDevice(shipmentId, d);

        Sending fakeSending = new Sending(deviceId, Instant.now(), true, true);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(sendingFactory.create()).thenReturn(fakeSending);

        Sending result = deviceService.ingestMockSending(deviceId);

        assertSame(fakeSending, result);
        assertEquals(1, shipment.getSendings().size());
        assertTrue(shipment.getSendings().contains(fakeSending));

        verify(sendingFactory).create();
    }

    @Test
    void ingestMockSending_throws_whenDeviceNotAssociated() {
        assertThrows(IllegalStateException.class, () ->
                deviceService.ingestMockSending(123L)
        );

        verifyNoInteractions(shipmentRepository);
        verifyNoInteractions(sendingFactory);
    }

    @Test
    void ingestMockSending_throws_whenShipmentNotFound() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", 1L);

        deviceService.addDevice(99L, d);
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                deviceService.ingestMockSending(1L)
        );

        verify(sendingFactory, never()).create();
    }
}
