package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.data.Sending;
import jakarta.transaction.Transactional;
import it.univr.track.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class DeviceService {

    // deviceId -> shipmentId
    private final Map<Long, Long> shipmentIdByDeviceId = new HashMap<>();

    private final ShipmentRepository shipmentRepository;
    private final SendingFactory sendingFactory;

    public DeviceService(ShipmentRepository shipmentRepository, SendingFactory sendingFactory) {
        this.shipmentRepository = shipmentRepository;
        this.sendingFactory = sendingFactory;
    }

    public void addDevice(Long shipmentId, Device d) {
        if (d.getId() == null) throw new IllegalArgumentException("Device id is null");
        shipmentIdByDeviceId.putIfAbsent(d.getId(), shipmentId);
    }

    public void removeDevice(Device d) {
        if (d.getId() == null) throw new IllegalArgumentException("Device id is null");
        Long removed = shipmentIdByDeviceId.remove(d.getId());
        if (removed == null) {
            throw new IllegalStateException("Device with id " + d.getId() + " is not registered");
        }
    }

    // Questo simula "il device invia un report"
    @Transactional
    public Sending ingestMockSending(Long deviceId) {
        Long shipmentId = shipmentIdByDeviceId.get(deviceId);
        if (shipmentId == null) throw new IllegalStateException("Device not associated to any shipment");

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        Sending sending = sendingFactory.create();  // genera un sending fake coerente
        shipment.addSending(sending);               // lo salvi dentro shipment (relazione JPA)

        return sending;
    }
}

