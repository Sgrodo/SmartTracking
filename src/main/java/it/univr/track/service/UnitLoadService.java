package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.DeviceAssignmentRepository;
import it.univr.track.repository.LoadItemRepository;
import it.univr.track.repository.UnitLoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Gestisce le UnitLoad.
 * - Creazione / lettura / cancellazione
 * - Cambio tracking state
 * - Binding device <-> UnitLoad tramite DeviceAssignment
 */
@Service
@RequiredArgsConstructor
public class UnitLoadService {

    private final UnitLoadRepository unitLoadRepository;
    private final LoadItemRepository loadItemRepository;
    private final DeviceAssignmentRepository deviceAssignmentRepository;

    public UnitLoad createUnitLoad(String description,
                                   ZonedDateTime startDate,
                                   Set<PhysicalConstraint> physicalConstraints,
                                   Collection<LoadItem> loadItems) {
        UnitLoad unitLoad = new UnitLoad(description, startDate, physicalConstraints);

        for( LoadItem item : loadItems ) {
            unitLoad.addLoadItem(item);
        }
        return unitLoadRepository.save(unitLoad);
    }

    @Transactional(readOnly = true)
    public UnitLoad getUnitLoadById(Long unitLoadId) {
        if (unitLoadId == null) throw new InvalidDataException("unitLoadId is null");
        return unitLoadRepository.findById(unitLoadId)
                .orElseThrow(() -> new NotFoundException("UnitLoad not found: " + unitLoadId));
    }

    @Transactional
    public boolean deleteUnitLoadById(Long unitLoadId) {
        if (unitLoadId == null) throw new InvalidDataException("unitLoadId is null");
        if (unitLoadRepository.existsById(unitLoadId)) {
            unitLoadRepository.deleteById(unitLoadId);
            return true;
        }
        throw new NotFoundException("UnitLoad not found: " + unitLoadId);
    }

    @Transactional(readOnly = true)
    public UnitLoad[] getAllUnitLoads() {
        return unitLoadRepository.findAll().toArray(new UnitLoad[0]);
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

    @Transactional
    public List<UnitLoad> getAllUnitloadsNotInShipments(){
        return unitLoadRepository.findAllNotInActiveShipments();
    }
    //TRACKING STATE

    @Transactional
    public void addLoadItem(Long unitLoadId, Long loadItemId) {
        UnitLoad ul = getUnitLoadById(unitLoadId);
        Optional<LoadItem> loadItem = loadItemRepository.findById(loadItemId);

        if (loadItem.isEmpty()) {
            throw new NotFoundException("LoadItem not found: " + loadItemId);
        }

        ul.addLoadItem(loadItem.get());
        unitLoadRepository.save(ul);
    }

    @Transactional
    public boolean removeLoadItemByQrCode(Long unitLoadId, String qrCode) {
        UnitLoad ul = getUnitLoadById(unitLoadId);
        boolean removed = ul.removeProductByQrCode(qrCode);
        unitLoadRepository.save(ul);
        return removed;
    }

}
