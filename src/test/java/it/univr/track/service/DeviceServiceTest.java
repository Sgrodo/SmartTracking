package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.data.Sending;
import it.univr.track.repository.DeviceRepository;
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
    private DeviceRepository deviceRepository;

    @BeforeEach
    void setUp() {
        shipmentRepository = mock(ShipmentRepository.class);
        deviceRepository = mock(DeviceRepository.class);
        sendingFactory = mock(SendingFactory.class);
        deviceService = new DeviceService(shipmentRepository, deviceRepository, sendingFactory);
    }

    // ---------- addDevice ----------

    @Test
    void addDevice_registersDeviceToShipmentId() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", 10L);

        assertDoesNotThrow(() -> deviceService.addDeviceToShipment(5L, d.getId()));
    }

    @Test
    void addDevice_throws_whenDeviceToShipmentIdNull() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");

        assertThrows(IllegalArgumentException.class, () ->
                deviceService.addDeviceToShipment(1L, d.getId())
        );
    }

    // ---------- removeDevice ----------

    @Test
    void removeDevice_removesRegisteredDeviceFromShipment() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", 7L);

        deviceService.addDeviceToShipment(3L, d.getId());

        assertDoesNotThrow(() -> deviceService.removeDeviceFromShipment(d.getId()));
    }

    @Test
    void removeDevice_throws_whenDeviceFromShipmentNotRegistered() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");
        ReflectionTestUtils.setField(d, "id", 99L);

        assertThrows(IllegalStateException.class, () ->
                deviceService.removeDeviceFromShipment(d.getId())
        );
    }

    @Test
    void removeDevice_throws_whenDeviceFromShipmentIdNull() {
        Device d = new Device(true, true, true, BigDecimal.ONE, "FORMULA");

        assertThrows(IllegalArgumentException.class, () ->
                deviceService.removeDeviceFromShipment(d.getId())
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

        deviceService.addDeviceToShipment(shipmentId, d.getId());

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

        deviceService.addDeviceToShipment(99L, d.getId());
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                deviceService.ingestMockSending(1L)
        );

        verify(sendingFactory, never()).create();
    }
}
