package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.data.Sending;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import it.univr.track.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DeviceService {

    private final ShipmentRepository shipmentRepository;
    private final DeviceRepository deviceRepository;
    private final SendingFactory sendingFactory;

    public DeviceService(ShipmentRepository shipmentRepository, DeviceRepository deviceRepository, SendingFactory sendingFactory) {
        this.shipmentRepository = shipmentRepository;
        this.deviceRepository = deviceRepository;
        this.sendingFactory = sendingFactory;
    }

    @Transactional
    public void addDeviceToShipment(Long shipmentId, Long deviceId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));

        shipment.addDevice(device);
    }


    @Transactional
    public void removeDeviceFromShipment(Long deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("deviceId is null");
        }

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Device not found: " + deviceId)
                );

        if (device.getShipment() == null) {
            throw new IllegalStateException(
                    "Device " + deviceId + " is not associated to any shipment"
            );
        }

        // rompe l'associazione (owning side)
        device.setShipment(null);

        // persiste la modifica
        deviceRepository.save(device);
    }


    @Transactional
    public void setDeviceTrackingState(Set<Device> devices, TrackingState state){
        for( Device d : devices){
            d.setTrackingState(state);
        }
    }

    // Questo simula "il device invia un report"
    @Transactional
    public Sending ingestMockSending(Long deviceId) {
        Long shipmentId = deviceRepository.findById(deviceId).orElseThrow(()->
                new IllegalArgumentException("Device not found: " + deviceId)).getId();
        if (shipmentId == null) throw new IllegalStateException("Device not associated to any shipment");

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        Sending sending = sendingFactory.create();  // genera un sending fake coerente
        shipment.addSending(sending);               // lo salvi dentro shipment (relazione JPA)

        return sending;
    }

    private void validateDevice(Device device) {
        if (device == null) {
            throw new IllegalArgumentException("Device is null");
        }
        if (device.getId() == null) {
            throw new IllegalArgumentException("Device id is null");
        }
    }

    public Device getDeviceById(Long deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("deviceId is null");
        }

        return deviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Device not found: " + deviceId)
                );
    }



}

