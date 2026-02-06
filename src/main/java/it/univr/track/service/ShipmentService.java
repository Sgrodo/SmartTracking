package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import it.univr.track.helper.Thrower;
import it.univr.track.repository.ShipmentRepository;
import it.univr.track.repository.TrackableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TrackableService trackableService;
    private final TrackableRepository trackableRepository;
    private final DeviceService deviceService;
    private final DeviceAssignmentService deviceAssignmentService;


    @Transactional
    public Shipment create(ZonedDateTime startDate, String description, Set<Trackable> trackables) {
        if(trackables == null || trackables.isEmpty()) {
            throw new InvalidDataException("trackables is null or empty");
        }
        Shipment shipment = new Shipment(startDate, description);
        shipment = shipmentRepository.save(shipment);

        for(Trackable tr : trackables){
            shipment.addTrackable(tr);
            trackableService.setShipment(tr.getId(),shipment.getId());
            //tr.setShipment(shipment);
            //trackableRepository.save(tr);
        }

        return shipmentRepository.save(shipment);
    }

    @Transactional
    public void completeShipment(Shipment shipment){
        completeShipment(shipment, ZonedDateTime.now());
    }

    @Transactional
    public void completeShipment(Shipment shipment, ZonedDateTime endDate) {
        if (shipment == null) throw new IllegalArgumentException("shipment is null");
        ZonedDateTime date = endDate == null ? ZonedDateTime.now() : endDate;

        Shipment s;
        if(shipment.getId() != null && shipmentRepository.existsById(shipment.getId())){
            s = shipmentRepository.getReferenceById(shipment.getId());
        }else{
            throw new NotFoundException("Shipment not found: " + shipment.getId());
        }
        s.complete(date);

        //set di Id dei Trackables associati allo shipment
        Set<Long> set = deviceAssignmentService.getTrackableIdsWithActiveDevice(s.getTrackables());
        Set<DeviceAssignment> associatedDevices = new HashSet<>();
        //di ogni trackable mi prendo i deviceAssignament
        for(Long id : set){
            associatedDevices = trackableService.getById(id).getDevices();
           for (DeviceAssignment da : associatedDevices){
               da.deactivate();
               //deviceAssignmentService.unbind(id);
           }

        }
        shipmentRepository.save(s);
    }

    @Transactional(readOnly = true)
    public Shipment getById(Long shipmentId) {
        if (shipmentId == null) throw new IllegalArgumentException("shipmentId is null");
        if(shipmentRepository.existsById(shipmentId)){
            return shipmentRepository.getReferenceById(shipmentId);
        }
        throw new NotFoundException("Shipment not found: " + shipmentId);
    }

    @Transactional
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @Transactional
    public void update(Shipment shipment, String description, ZonedDateTime endDate, ShipmentState state, Set<Trackable> trackables) {
        if(trackables == null || trackables.isEmpty()) {
            throw new InvalidDataException("trackables is null or empty");
        }
        if (shipment == null) {
            throw new InvalidDataException("shipment is null");
        }

        Set<Trackable> newTrackables = new HashSet<>(trackables);

        Shipment s;
        if(shipment.getId() != null && shipmentRepository.existsById(shipment.getId())){
            s = shipmentRepository.getReferenceById(shipment.getId());
        }else{
            throw new NotFoundException("Shipment not found: " + shipment.getId());
        }

        HashSet<Trackable> toRemove = new HashSet<Trackable>(s.getTrackables());
        for (Trackable t : toRemove) {
            s.removeTrackable(t);
        }

        for (Trackable tr : newTrackables) {
            Trackable managed = trackableRepository.findById(tr.getId())
                    .orElseThrow(() -> new NotFoundException("Trackable not found: " + tr.getId()));
            s.addTrackable(managed);
            managed.setShipment(s);
            trackableRepository.save(tr);
        }

        s.setState(state);
        s.setDescription(description);
        //s è managed, save non necessario (in teoria)
        shipmentRepository.save(s);
    }

    @Transactional
    public boolean deleteById(Long shipmentId) {
        Thrower.notNullArgumentException(shipmentId, "shipmentId is null");

        if (! shipmentRepository.existsById(shipmentId)) {
            throw new NotFoundException("Shipment not found: " + shipmentId);
        }
        shipmentRepository.getReferenceById(shipmentId).getTrackables().
                forEach((trackable)->{
            trackable.setShipment(null);
        });
        shipmentRepository.deleteById(shipmentId);
        return true;

    }

    @Transactional
    public void setTrackingState(Shipment shipment, TrackingState state) {
        HashSet<Device> devices = new HashSet<>();
        for(Trackable tr : shipment.getTrackables()){
            for (DeviceAssignment deviceAssignment : tr.getDevices()){
                devices.add(deviceAssignment.getDevice());
            }
        }
        deviceService.setDeviceTrackingState(devices, state);
    }

    @Transactional
    public TrackingState getTrackingState(Shipment shipment) {
        for(Trackable tr : shipment.getTrackables()){
            for (DeviceAssignment deviceAssignment : tr.getDevices()){
                return deviceAssignment.getDevice().getTrackingState();
            }
        }
        return null;
    }

    @Transactional
    public void setTrackingState(Long shipmentId, TrackingState state) {
        this.setTrackingState(this.getById(shipmentId), state);
    }

}
