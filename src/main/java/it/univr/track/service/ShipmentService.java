package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.PhysicalConstraintEmbeddable;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * Questa Classe si occupa della gestione delle spedizioni e verrà utilizzata
 * direttamente dal controller che dovrà effettuare:
 *
 creazione spedizioni
 lista spedizioni
 attiva/disattiva tracking
 associazione device/spedizione

 */
@Service
public final class ShipmentService {
    private final DeviceService deviceService;
    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository, DeviceService deviceService) {
        this.shipmentRepository = shipmentRepository;
        this.deviceService = deviceService;
    }

    /**
     * Crea e registra una nuova Shipment nel sistema.
     */
    public Shipment generateShipment(
            String description,
            ZonedDateTime startDate,
            Set<PhysicalConstraint> physicalConstraints
    ) {
        // le validazioni principali sono già nel costruttore di Shipment
        Shipment shipment = new Shipment(description, startDate, physicalConstraints);

        return shipmentRepository.save(shipment);
    }

    @Transactional
    public void associateDevice(Long shipmentId, Device d) {
        Shipment s = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        s.addDevice(d);
        deviceService.addDevice(shipmentId, d);
    }
    /**
     * Ogni device può essere associato al più ad una spedizione
     */
    @Transactional
    public void deAssociateDevice(Long shipmentId, Device d) {
        Shipment s = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        s.removeDevice(d);
        deviceService.removeDevice(d);
    }

    @Transactional
    public Set<Device> getDevicesOfShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow()
                .getDevices();
    }

}
