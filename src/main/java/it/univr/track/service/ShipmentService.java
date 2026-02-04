package it.univr.track.service;

import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import it.univr.track.helper.Thrower;
import it.univr.track.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TrackableService trackableService;


    @Transactional
    public Shipment create(ZonedDateTime startDate, String description, Set<Trackable> trackables) {
        if(trackables == null || trackables.isEmpty()) {
            throw new InvalidDataException("trackables is null or empty");
        }
        Shipment shipment = new Shipment(startDate, description);
        shipment = shipmentRepository.save(shipment);

        for(Trackable tr : trackables){
            shipment.addTrackable(tr);
        }

        return shipment;
    }

    @Transactional(readOnly = true)
    public Shipment getById(Long shipmentId) {
        if (shipmentId == null) throw new IllegalArgumentException("shipmentId is null");
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new NotFoundException("Shipment not found: " + shipmentId));
    }

    @Transactional
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @Transactional
    public void update(Shipment shipment, String description, ZonedDateTime endDate, ShipmentState state, Set<Trackable> trackables) {
        shipment.setState(state);
        shipment.setDescription(description);
        shipment.setTrackables(trackables);

        shipmentRepository.save(shipment);
    }

    @Transactional
    public boolean deleteById(Long shipmentId) {
        Thrower.notNullArgumentException(shipmentId, "shipmentId is null");
        if (shipmentRepository.existsById(shipmentId)) {
            shipmentRepository.deleteById(shipmentId);
            return true;
        }
        throw new NotFoundException("Shipment not found: " + shipmentId);
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
