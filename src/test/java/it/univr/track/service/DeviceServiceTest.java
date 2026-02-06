package it.univr.track.service;

import io.cucumber.java.hu.De;
import it.univr.track.BaseTest;
import it.univr.track.entity.Device;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.exception.InvalidDataException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Molti test del device vengono fatti
 */
public class DeviceServiceTest extends BaseTest {

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private DeviceAssignmentService deviceAssignmentService;


    @BeforeEach
    public void tearDown() {
        deviceRepository.deleteAll();
        unitLoadRepository.deleteAll();
        shipmentRepository.deleteAll();
    }

    @Transactional
    @Test
    public void testGetDevices(){
        assertThrows(InvalidDataException.class, () ->
                deviceService.getDeviceById(null));

        Device device = new Device(true,true,true,
                new BigDecimal("1.0"),"origin");

            device = deviceRepository.save(device);
            assertEquals(1, deviceService.getAvailableDevices().size());
    }

    @Transactional
    @Test
    public void testCoherenceBetweenDeviceAndAssignment(){
        Device device = new Device(true,true,true,
                new BigDecimal("1.0"),"origin");

        device = deviceRepository.save(device);
        UnitLoad t = unitLoadRepository.save(new UnitLoad());
        assertEquals(TrackingState.INACTIVE, device.getTrackingState());

        //all'inizio il device è disponibile
        assertEquals(1, deviceService.getAvailableDevices().size());

        Shipment shipment = shipmentRepository.save(new Shipment("Olive"));
        //creo uno shipment e lo imposto in transito
        shipmentService.update(shipment,null,null, ShipmentState.IN_TRANSIT, Set.of(t));
        //associo il device al trackable
        deviceAssignmentService.bind(device.getId(), t.getId());
        //ora il device non è più disponibile
        assertEquals(0, deviceService.getAvailableDevices().size());



    }

    @Transactional
    @Test
    public void testSetDevices(){
        Device device = new Device(true,true,true,
                new BigDecimal("1.0"),"origin");

        device = deviceRepository.save(device);

       assertThrows(InvalidDataException.class, () ->
                deviceService.setDeviceTrackingState(null, TrackingState.ACTIVE));

        assertThrows(InvalidDataException.class, () ->
                deviceService.setDeviceTrackingState(new HashSet<>(), TrackingState.ACTIVE));

        Device finalDevice = device;

        assertThrows(InvalidDataException.class, () ->
                deviceService.setDeviceTrackingState(Set.of(finalDevice), null));

        deviceService.setDeviceTrackingState(Set.of(finalDevice), TrackingState.ACTIVE);

        assertEquals(TrackingState.ACTIVE, deviceService.getDeviceById(device.getId()).getTrackingState());
    }
}
