package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.repository.DeviceAssignmentRepository;
import it.univr.track.repository.UnitLoadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gestisce le UnitLoad.
 * - Creazione / lettura / cancellazione
 * - Cambio tracking state
 * - Binding device <-> UnitLoad tramite DeviceAssignment
 */
@Service
public class UnitLoadService {

    private final UnitLoadRepository unitLoadRepository;
    private final DeviceAssignmentRepository deviceAssignmentRepository;
    private final DeviceAssignmentService deviceAssignmentService;

    public UnitLoadService(UnitLoadRepository unitLoadRepository,
                           DeviceAssignmentService deviceAssignmentService,
                           DeviceAssignmentRepository deviceAssignmentRepository) {
        this.unitLoadRepository = unitLoadRepository;
        this.deviceAssignmentService = deviceAssignmentService;
        this.deviceAssignmentRepository = deviceAssignmentRepository;
    }


    public UnitLoad createUnitLoad(String description,
                                   ZonedDateTime startDate,
                                   Set<PhysicalConstraint> physicalConstraints) {
        UnitLoad unitLoad = new UnitLoad(description, startDate, physicalConstraints);
        return unitLoadRepository.save(unitLoad);
    }

    @Transactional(readOnly = true)
    public UnitLoad getUnitLoadById(Long unitLoadId) {
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");
        return unitLoadRepository.findById(unitLoadId)
                .orElseThrow(() -> new IllegalArgumentException("UnitLoad not found: " + unitLoadId));
    }

    @Transactional
    public boolean deleteUnitLoadById(Long unitLoadId) {
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");
        if (unitLoadRepository.existsById(unitLoadId)) {
            unitLoadRepository.deleteById(unitLoadId);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public UnitLoad[] getAllUnitLoads() {
        return unitLoadRepository.findAll().toArray(new UnitLoad[0]);
    }

    // ---- DEVICE BINDING ----

    /**
     * Associa un device a questa UnitLoad.
     * Regola-Constraint: un device ha al massimo 1 assignment attivo -> gestita nel DeviceAssignmentService.
     */
    @Transactional
    public void associateDevice(Long unitLoadId, Long deviceId) {
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");

        // fail-fast: unitload deve esistere
        getUnitLoadById(unitLoadId);

        deviceAssignmentService.bind(deviceId, unitLoadId);
    }

    /**
     * Disassocia (unbind) il device senza imporre da quale UnitLoad venga.
     * Utile se l'endpoint ti passa solo deviceId.
     */
    @Transactional
    public void deAssociateDevice(Long deviceId) {
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");
        deviceAssignmentService.unbind(deviceId);
    }

    /**
     * Disassocia il device SOLO se è attivo su questa UnitLoad (preserva semantica vecchio endpoint pre-refactor).
     * Credo sia meglio usare questo per andare più sicuri.
     */
    @Transactional
    public void deAssociateDeviceFromUnitLoad(Long unitLoadId, Long deviceId) {
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");

        UnitLoad ul = getUnitLoadById(unitLoadId);

        Trackable activeTarget = deviceAssignmentService.getActiveTarget(deviceId, ZonedDateTime.now());
        if (!activeTarget.getId().equals(ul.getId())) {
            throw new IllegalStateException("Device " + deviceId + " is not allocated to UnitLoad " + unitLoadId);
        }

        deviceAssignmentService.unbind(deviceId);
    }

    /**
     * Restituisce i device attualmente assegnati a questa UnitLoad (attivi).
     * Sostituisce il vecchio unitLoad.getDevices().
     */
    @Transactional(readOnly = true)
    public Set<Device> getActiveDevicesOfUnitLoad(Long unitLoadId) {
        UnitLoad unitLoad = getUnitLoadById(unitLoadId);

        // Recupera tutte le associazioni Device–UnitLoad ancora attive
        List<DeviceAssignment> activeAssignments =
                deviceAssignmentRepository.findByTargetAndToTsIsNull(unitLoad);

        // Estrae i Device associati a tali assegnazioni
        Set<Device> activeDevices = new HashSet<>();
        for (DeviceAssignment assignment : activeAssignments) {
            activeDevices.add(assignment.getDevice());
        }

        return activeDevices;
    }


    // ---- TRACKING STATE ----

    /**
     * Cambia stato di tracking della UnitLoad.
     * Nota: non propagare sui device: i device sono riusabili e non "appartengono" alla unitload.
     */
    @Transactional
    public void setTrackingState(Long unitLoadId, TrackingState state) {
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");
        if (state == null) throw new IllegalArgumentException("state is null");

        UnitLoad ul = getUnitLoadById(unitLoadId);
        ul.setTrackingState(state);
        unitLoadRepository.save(ul);
    }
}
