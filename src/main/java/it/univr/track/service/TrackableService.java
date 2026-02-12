package it.univr.track.service;

import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.ShipmentRepository;
import it.univr.track.repository.TrackableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TrackableService {

    private final TrackableRepository trackableRepository;
    private final ShipmentRepository shipmentRepository;

    public TrackableService(TrackableRepository trackableRepository, ShipmentRepository shipmentRepository) {
        this.trackableRepository = trackableRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public Set<UnitLoad> getUnitLoadsFromTrackableIds(Set<Long> trackableIds) {
        List<Trackable> unitLoadList = trackableRepository.findAllById(trackableIds);
        Set<UnitLoad> result = new HashSet<>();

        for (Trackable t : unitLoadList) {
            result.addAll(t.getUnitLoads());
        }

        return result;
    }

    @Transactional
    public Set<Trackable> getAllTrackablesById(Iterable<Long> trackableIds) {
        return new HashSet<>(trackableRepository.findAllById(trackableIds));
    }

    @Transactional(readOnly = true)
    public Trackable getById(Long id) {
        return trackableRepository.findById(id).orElseThrow(() -> new NotFoundException("No trackable found with id " + id));
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (! trackableRepository.existsById(id)) {
            throw new NotFoundException("Trackable " + id + " not found");
        }
        trackableRepository.deleteById(id);
        return true;
    }


    @Transactional
    public void setShipment(Long trackableId, Long shipmentId) {
        Trackable trackable = trackableRepository.findById(trackableId).orElseThrow(
                () -> new NotFoundException("Trackable " + trackableId + " not found"));
        Shipment s = shipmentRepository.getReferenceById(shipmentId);
        trackable.setShipment(s);
    }

    @Transactional
    public void freeTrackablesFromShipments(Long shipmentId) {
        List<Trackable> trackables = trackableRepository.findAllByShipmentId(shipmentId);

        for (Trackable t : trackables) {
            t.setShipment(null);
        }
    }
}
