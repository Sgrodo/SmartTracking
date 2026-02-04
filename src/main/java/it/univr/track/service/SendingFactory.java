package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.data.Trackable;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.entity.data.Sending;
import it.univr.track.repository.TrackableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;

/**
 * Gestisce la generazione dei Sending
 */
@Service
@RequiredArgsConstructor
public class SendingFactory {

    private final DeviceRepository deviceRepository;
//    private final DeviceAssignmentService deviceAssignmentService;
    private final TrackableRepository trackableRepository;

    @Transactional
    public Sending ingestSending(Long deviceId) {
        return null;
//        ZonedDateTime now = ZonedDateTime.now();
//
//        Device device = deviceRepository.findById(deviceId)
//                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));
//
//        Trackable target = deviceAssignmentService.getActiveTarget(deviceId, now);
//
//        // crea sending (editable...)
//        Sending sending = new Sending(device, now, true, true);
//
//        // salva “sul target” (UnitLoad o Package, uguale)
//        target.addSending(sending);
//
//        // siccome target è entity gestita, basta salvarla (o lasciare che la transazione flushi)
//        trackableRepository.save(target);
//        return sending;
    }
}


