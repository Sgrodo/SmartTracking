package it.univr.track.service;

import it.univr.track.repository.ShipmentRepository;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.data.Sending;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.Instant;

/**
 * Gestisce la generazione dei Sending
 */
@Service
public class SendingFactory {

    private final ShipmentRepository shipmentRepository;

    public SendingFactory(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public void ingestSending(Long shipmentId, Long deviceId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        Sending sending = new Sending(deviceId, Instant.now(), true, true);
        shipment.addSending(sending);
    }

    public Sending create() {
        // TODO vedere se fare un random con valori preimpostati o qualcosa del genere
        return new Sending(
                10L,
                Instant.now(),
                true,
                true
        );
    }
}

