package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.repository.DeviceAssignmentRepository;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.TrackableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class DeviceAssignmentService {

    private final DeviceRepository deviceRepository;
    private final TrackableRepository trackableRepository;
    private final DeviceAssignmentRepository assignmentRepository;

    /***
     * overload del comando, in automatico usa NOW
     * @param deviceId
     * @param trackableId
     */
    @Transactional
    public void bind(Long deviceId, Long trackableId) {
        bind(deviceId, trackableId, ZonedDateTime.now());
    }

    /***
     * ATTENZIONE se il device è in uso viene CHIUSO IL BIND ATTUALE e aperto uno nuovo.
     * Un device appartiene SOLO ad una UnilLoad OPPURE ad un Package (collezione di UnitLoad)
     */
    @Transactional
    public void bind(Long deviceId, Long trackableId, ZonedDateTime now) {
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");
        if (trackableId == null) throw new IllegalArgumentException("trackableId is null");
        if (now == null) throw new IllegalArgumentException("now is null");

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));

        Trackable target = trackableRepository.findById(trackableId)
                .orElseThrow(() -> new IllegalArgumentException("Trackable not found: " + trackableId));

        // chiudi quello attivo, se esiste (constraint logico)
        assignmentRepository.findFirstByDeviceAndToTsIsNull(device)
                .ifPresent(a -> a.close(now));

        assignmentRepository.save(new DeviceAssignment(device, target, now));
    }

    /***
     *Oveloading il timestamp usato è NOW
     */
    @Transactional
    public void unbind(Long deviceId) {
        unbind(deviceId, ZonedDateTime.now());
    }

    @Transactional
    public void unbind(Long deviceId, ZonedDateTime now) {
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");
        if (now == null) throw new IllegalArgumentException("now is null");

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));

        DeviceAssignment active = assignmentRepository.findFirstByDeviceAndToTsIsNull(device)
                .orElseThrow(() -> new IllegalStateException("Device " + deviceId + " has no active assignment"));

        active.close(now);

        assignmentRepository.save(active);
    }

    /***
     * Cerca il device attivo tramite l'istante di tempo passato come parametro, il quale deve essere
     * > dell'istante in cui è stato assegnato e il timestamp di fine deve essere a null (spedizione non finita)
     * @param deviceId id del device
     * @param ts timestamp from
     * @return il Trackable di riferimento...
     */
    @Transactional(readOnly = true)
    public Trackable getActiveTarget(Long deviceId, ZonedDateTime ts) {
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");
        if (ts == null) throw new IllegalArgumentException("ts is null");

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));

        return assignmentRepository.findActiveAt(device, ts)
                .orElseThrow(() -> new IllegalStateException("No active assignment for device " + deviceId + " at " + ts))
                .getTarget();
    }
}
