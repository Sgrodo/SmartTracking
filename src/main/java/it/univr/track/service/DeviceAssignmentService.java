package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import it.univr.track.helper.Thrower;
import it.univr.track.repository.DeviceAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceAssignmentService {

    private final DeviceService deviceService;
    private final TrackableService trackableService;
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
    public void bind(Long deviceId, Long trackableId, ZonedDateTime time) throws IllegalStateException {
        Thrower.notNullArgumentException(deviceId, "deviceId is null");
        Thrower.notNullArgumentException(trackableId, "trackableId is null");
        ZonedDateTime now = time != null ? time : ZonedDateTime.now();

        Device device = deviceService.getDeviceById(deviceId);
        if (this.isDeviceAssigned(device)) {
            throw new InvalidDataException("Device " + deviceId + " is already assigned");
        }

        Trackable target = trackableService.getById(trackableId);
        if(target.getShipment() != null && target.getShipment().isFinalState()){
            throw new InvalidDataException("Cannot assign device to trackable " + trackableId + " because its shipment is in " + target.getShipment().getState());
        }
        DeviceAssignment deviceAssignment = new DeviceAssignment(device, target, now);
        target.getDevices().add(deviceAssignment);

        assignmentRepository.save(deviceAssignment);
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
        Thrower.notNullArgumentException(deviceId, "deviceId is null");

        Device device = deviceService.getDeviceById(deviceId);

        DeviceAssignment active = assignmentRepository.findFirstByDeviceAndToTsIsNull(device)
                .orElseThrow(() -> new InvalidDataException("Device " + deviceId + " has no active assignment"));

        active.deactivate(now);

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
        Thrower.notNullArgumentException(deviceId, "deviceId is null");
        Thrower.notNullArgumentException(ts, "ts is null");

        Device device = deviceService.getDeviceById(deviceId);

        return assignmentRepository.findActiveAt(device, ts)
                .orElseThrow(() -> new NotFoundException("No active assignment for device " + deviceId + " at " + ts))
                .getTarget();
    }

    @Transactional(readOnly = true)
    public Set<DeviceAssignment> getActiveDeviceAssignmentsByTrackables(Collection<Trackable> trackables) {
        if (trackables == null || trackables.isEmpty()) return Set.of();

        List<Long> ids = trackables.stream().map(Trackable::getId).toList();
        List<DeviceAssignment> active = assignmentRepository.findActiveByTargetIds(ids);

        return new HashSet<>(active);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<DeviceAssignment>> getActiveAssignmentsByTrackables(Set<Trackable> trackables) {
        if (trackables == null || trackables.isEmpty()) return Map.of();

        List<Long> ids = trackables.stream().map(Trackable::getId).toList();
        List<DeviceAssignment> active = assignmentRepository.findActiveByTargetIds(ids);

        return active.stream()
                .collect(Collectors.groupingBy(a -> a.getTarget().getId()));
    }



    @Transactional
    public boolean isDeviceAssigned(Device device) {
        Thrower.notNullArgumentException(device, "device is null");

        return assignmentRepository
                .findFirstByDeviceAndToTsIsNull(device)
                .isPresent();
    }

}
