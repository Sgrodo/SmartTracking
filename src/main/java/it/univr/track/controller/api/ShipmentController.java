package it.univr.track.controller.api;

import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.service.UnitLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@Controller
public class ShipmentController {

    private final UnitLoadService unitLoadService;

    public ShipmentController(UnitLoadService unitLoadService){
        this.unitLoadService = unitLoadService;
    }
    // create a shipment
    @PostMapping("/shipment")
    public boolean createShipment(@RequestParam String description,
                                  @RequestParam ZonedDateTime startDate,
                                  @RequestParam Set<PhysicalConstraint> constraint) {
        try {
            unitLoadService.createUnitLoad(description, startDate, constraint);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed");
        }
    }

    // read a shipment
    @GetMapping("/shipment/{shipmentId}")
    public UnitLoad readShipment(@PathVariable Long shipmentId) {
        try {
            return unitLoadService.getUnitLoadById(shipmentId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Read failed");
        }
    }

    //TODO
    // update a shipment
    @PutMapping("/api/shipment")
    public boolean editShipment(@RequestParam Optional<String> descri) {
        return true;
    }

    /**
     *
     * @param shipmentId Id of a Shipment
     * @return false mean that does not exist a Shipment of that Id
     */
    // update a shipment
    @DeleteMapping("/api/shipment")
    public boolean deleteShipment(@RequestParam Long shipmentId) {
        return unitLoadService.deleteUnitLoadById(shipmentId);
    }

    // list all the shipments
    @GetMapping("/api/shipments")
    public UnitLoad[] readShipments() {
        return unitLoadService.getAllUnitLoads();
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
            unitLoadService.setTrackingState(shipmentId,tState);
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
        try {
            unitLoadService.getUnitLoadById(shipmentId); // solo per validare che esista
            unitLoadService.associateDevice(shipmentId, deviceId);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossible to find a Shipment of Id " + shipmentId + ": " + e.getMessage()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Request rejected");
        }
    }


    // deallocate a device to a shipment
    @PostMapping("/api/shipment/deallocate")
    public boolean deallocateDevice(@RequestParam Long deviceId, @RequestParam Long shipmentId) {
        try {
            //TODO CHECK
            // shipmentId lo ignoriamo (o lo usiamo solo per check esistenza)
            unitLoadService.deAssociateDevice(deviceId);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Request rejected");
        }
    }

}
