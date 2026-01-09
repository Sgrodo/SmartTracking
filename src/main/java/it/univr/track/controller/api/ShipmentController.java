package it.univr.track.controller.api;

import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import it.univr.track.entity.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Set;

@Controller
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService){
        this.shipmentService = shipmentService;
    }
    // create a shipment
    @PostMapping("/shipment")
    public boolean createShipment(@RequestParam String description,
                                  @RequestParam ZonedDateTime startDate,
                                  @RequestParam Set<PhysicalConstraint> constraint) {
        try {
            shipmentService.createShipment(description, startDate, constraint);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed");
        }
    }

    // read a shipment
    @GetMapping("/shipment/{shipmentId}")
    public Shipment readShipment(@PathVariable Long shipmentId) {
        try {
            return shipmentService.getShipmentById(shipmentId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Read failed");
        }
    }

    //TODO
    // update a shipment
    @PutMapping("/api/shipment")
    public boolean editShipment() {
        return true;
    }

    /**
     *
     * @param shipmentId Id of a Shipment
     * @return false mean that does not exist a Shipment of that Id
     */
    // update a shipment
    @DeleteMapping("/api/shipment")
    public boolean deleteShipment(Long shipmentId) {
        return shipmentService.deleteShipmentById(shipmentId);
    }

    // list all the shipments
    @GetMapping("/api/shipments")
    public Shipment[] readShipments() {
        return shipmentService.getAllShipments();
    }

    // start/stop tracking
    @PostMapping("/api/shipment/tracking")
    public boolean setShipmentTracking(@RequestParam Long shipmentId, @RequestParam boolean state) {
        TrackingState tState;

        if(state){
            tState = TrackingState.ACTIVE;
        }else{
            tState = TrackingState.INACTIVE;
        }
        try {
            shipmentService.setTrackingState(shipmentId,tState);
            return true;
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Impossible to set TrackingState" + e.getMessage());
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Read failed");
        }
    }

    // allocate a device to a shipment
    @PostMapping("/api/shipment/allocate")
    public boolean allocateDevice(@RequestParam Long shipmentId, @RequestParam Long deviceId) {
        Shipment shipment;
        try {
            shipment = shipmentService.getShipmentById(shipmentId);
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Impossible to find a Shipment of Id" + shipmentId.toString() + e.getMessage());
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Read failed");
        }
        shipmentService.getDevicesOfShipment(shipment.getId());
        shipmentService.associateDevice(shipment.getId(),deviceId);
        return true;
    }

    // deallocate a device to a shipment
    @PostMapping("/api/shipment/deallocate")
    public boolean deallocateDevice() {
        return true;
    }
}
