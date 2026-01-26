package it.univr.track.service;

import it.univr.track.controller.api.dto.CreateShipmentRequest;
import it.univr.track.controller.api.dto.UpdateShipmentRequest;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.repository.ShipmentRepository;
import it.univr.track.repository.TrackableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TrackableRepository trackableRepository;

    public ShipmentService(ShipmentRepository shipmentRepository,
                           TrackableRepository trackableRepository) {
        this.shipmentRepository = shipmentRepository;
        this.trackableRepository = trackableRepository;
    }

    @Transactional
    public Shipment create(CreateShipmentRequest req) {
        if (req == null) throw new IllegalArgumentException("request is null");
        if (req.getStartDate() == null) throw new IllegalArgumentException("startDate is required");
        if (req.getDescription() == null || req.getDescription().isBlank())
            throw new IllegalArgumentException("description is required");

        Shipment s = new Shipment(req.getStartDate(), req.getDescription());
        return shipmentRepository.save(s);
    }

    @Transactional(readOnly = true)
    public Shipment getById(Long shipmentId) {
        if (shipmentId == null) throw new IllegalArgumentException("shipmentId is null");
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));
    }

    @Transactional
    public void update(Long shipmentId, UpdateShipmentRequest req) {
        if (req == null) throw new IllegalArgumentException("request is null");

        Shipment s = getById(shipmentId);

        // description
        if (req.getDescription() != null ) {
            s.edit(req.getDescription(), null, null);
        }
    }

    @Transactional
    public void attachTrackable(Long shipmentId, Long trackableId) {
        Shipment s = getById(shipmentId);
        Trackable t = getTrackable(trackableId);

        // se già assegnato a questa shipment: no-op
        if (t instanceof UnitLoad ul) {
            if (ul.getShipment() != null && ul.getShipment().getId().equals(shipmentId)) return;
            if (ul.getShipment() != null && !ul.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("UnitLoad " + trackableId + " already belongs to another Shipment");
            }
            s.addUnitLoad(ul);
            return;
        }

        if (t instanceof Package pkg) {
            if (pkg.getShipment() != null && pkg.getShipment().getId().equals(shipmentId)) return;
            if (pkg.getShipment() != null && !pkg.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("Package " + trackableId + " already belongs to another Shipment");
            }
            s.addPackage(pkg);
            return;
        }

        throw new IllegalStateException("Unsupported Trackable type for id " + trackableId);
    }

    @Transactional
    public void detachTrackable(Long shipmentId, Long trackableId) {
        Shipment s = getById(shipmentId);
        Trackable t = getTrackable(trackableId);

        if (t instanceof UnitLoad ul) {
            if (ul.getShipment() == null || !ul.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("UnitLoad " + trackableId + " is not in Shipment " + shipmentId);
            }
            s.removeUnitLoad(ul);
            return;
        }

        if (t instanceof Package pkg) {
            if (pkg.getShipment() == null || !pkg.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("Package " + trackableId + " is not in Shipment " + shipmentId);
            }
            s.removePackage(pkg);
            return;
        }

        throw new IllegalStateException("Unsupported Trackable type for id " + trackableId);
    }

    private Trackable getTrackable(Long trackableId) {
        if (trackableId == null) throw new IllegalArgumentException("trackableId is null");
        return trackableRepository.findById(trackableId)
                .orElseThrow(() -> new IllegalArgumentException("Trackable not found: " + trackableId));
    }

    private boolean legalDateFields(Long shipmentId){
        if ( shipmentRepository.findById(shipmentId).isPresent()){
            Shipment s = shipmentRepository.getReferenceById(shipmentId);
            return (s.getStartDate().isBefore(s.getEndDate()));

        }else{
            throw new IllegalArgumentException("Shipment of id "+ shipmentId + " does not exist");
        }
    }

    @Transactional
    public boolean deleteUnitLoadById(Long unitLoadId) {
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");
        if (shipmentRepository.existsById(unitLoadId)) {
            shipmentRepository.deleteById(unitLoadId);
            return true;
        }
        return false;
    }
}
