package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShipmentServiceTest {

    private ShipmentRepository shipmentRepository;
    private DeviceService deviceService;
    private ShipmentService shipmentService;

    @BeforeEach
    void setUp() {
        shipmentRepository = mock(ShipmentRepository.class);
        deviceService = mock(DeviceService.class);
        shipmentService = new ShipmentService(shipmentRepository, deviceService);
    }

    @Test
    void generateShipment_savesAndReturnsShipment() {
        ZonedDateTime start = ZonedDateTime.now();
        Set<PhysicalConstraint> constraints = Set.of(new HumidityConstraint(60.0));

        // ritorna lo stesso oggetto salvato (semplifico)
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(inv -> inv.getArgument(0));

        Shipment created = shipmentService.generateShipment("desc", start, constraints);

        assertNotNull(created);
        assertEquals("desc", created.getDescription());
        assertEquals(start, created.getStartDate());
        assertEquals(1, created.getPhysicalConstraints().size());

        // verifica che save sia chiamato con uno Shipment
        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository).save(captor.capture());
        assertEquals("desc", captor.getValue().getDescription());
    }

    @Test
    void generateShipment_throws_whenStartDateNull() {
        assertThrows(IllegalArgumentException.class, () ->
                shipmentService.generateShipment("desc", null, Set.of())
        );

        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void associateDevice_addsDeviceAndCallsDeviceService() {
        Long shipmentId = 1L;
        Shipment s = new Shipment("x", ZonedDateTime.now(), Set.of());

        Device d = new Device(true, true, true, new BigDecimal("10.00"), "FORMULA");
        assertNull(d.getShipment());

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(s));

        shipmentService.associateDevice(shipmentId, d);

        // lato dominio: device associato
        assertTrue(s.getDevices().contains(d));
        assertEquals(s, d.getShipment());

        // lato collaborazione: chiamata a DeviceService
        verify(deviceService).addDevice(shipmentId, d);
    }

    @Test
    void associateDevice_throws_whenShipmentNotFound() {
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        Device d = new Device(true, true, true, new BigDecimal("10.00"), "FORMULA");

        assertThrows(IllegalArgumentException.class, () ->
                shipmentService.associateDevice(99L, d)
        );

        verify(deviceService, never()).addDevice(anyLong(), any());
    }

    @Test
    void deAssociateDevice_removesDeviceAndCallsDeviceService() {
        Long shipmentId = 1L;
        Shipment s = new Shipment("x", ZonedDateTime.now(), Set.of());

        Device d = new Device(true, true, true, new BigDecimal("10.00"), "FORMULA");
        s.addDevice(d);
        assertEquals(s, d.getShipment());

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(s));

        shipmentService.deAssociateDevice(shipmentId, d);

        assertFalse(s.getDevices().contains(d));
        assertNull(d.getShipment());

        verify(deviceService).removeDevice(d);
    }

    @Test
    void deAssociateDevice_throws_whenShipmentNotFound() {
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        Device d = new Device(true, true, true, new BigDecimal("10.00"), "FORMULA");

        assertThrows(IllegalArgumentException.class, () ->
                shipmentService.deAssociateDevice(99L, d)
        );

        verify(deviceService, never()).removeDevice(any());
    }

    @Test
    void getDevicesOfShipment_returnsDevices() {
        Long shipmentId = 1L;
        Shipment s = new Shipment("x", ZonedDateTime.now(), Set.of());

        Device d1 = new Device(true, true, true, new BigDecimal("10.00"), "FORMULA");
        Device d2 = new Device(true, true, true, new BigDecimal("20.00"), "FORMULA");
        s.addDevice(d1);
        s.addDevice(d2);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(s));

        Set<Device> devices = shipmentService.getDevicesOfShipment(shipmentId);

        assertEquals(2, devices.size());
        assertTrue(devices.contains(d1));
        assertTrue(devices.contains(d2));
    }

    @Test
    void getDevicesOfShipment_throws_whenShipmentNotFound() {
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                shipmentService.getDevicesOfShipment(99L)
        );
    }
}
