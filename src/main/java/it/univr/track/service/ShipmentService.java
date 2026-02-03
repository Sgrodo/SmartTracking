package it.univr.track.service;

import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.helper.Thrower;
import it.univr.track.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TrackableService trackableService;

    public ShipmentService(ShipmentRepository shipmentRepository,
                           TrackableService trackableService) {
        this.shipmentRepository = shipmentRepository;
        this.trackableService = trackableService;
    }

    @Transactional
    public Shipment create(ZonedDateTime startDate, String description, Set<Trackable> trackables) {
        Shipment s = new Shipment(startDate, description, trackables);
        return shipmentRepository.save(s);
    }

    @Transactional(readOnly = true)
    public Shipment getById(Long shipmentId) {
        if (shipmentId == null) throw new IllegalArgumentException("shipmentId is null");
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));
    }

    @Transactional
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @Transactional
    public void update(Shipment shipment, String description, ZonedDateTime endDate, ShipmentState state, Set<Trackable> trackables) {
        if (state.equals(ShipmentState.CANCELLED)) {
            shipment.cancel();
        } else if (state.equals(ShipmentState.COMPLETED)) {
            shipment.complete();
        }

        shipment.setDescription(description);
        shipment.setTrackables(trackables);

        shipmentRepository.save(shipment);
    }

    @Transactional
    public boolean deleteUnitLoadById(Long unitLoadId) {
        Thrower.notNullArgumentException(unitLoadId, "unitLoadId is null");
        if (shipmentRepository.existsById(unitLoadId)) {
            shipmentRepository.deleteById(unitLoadId);
            return true;
        }
        return false;
    }

    @Transactional
    public void setTrackingState(Shipment shipment, TrackingState state) {
        for(Trackable tr : shipment.getTrackables()){
            for (DeviceAssignment deviceAssignment : tr.getDevices()){
                deviceAssignment.getDevice().setTrackingState(state);
            }
        }
    }

    @Transactional
    public void setTrackingState(Long shipmentId, TrackingState state) {
        this.setTrackingState(this.getById(shipmentId), state);
    }

}
