package it.univr.track.controller.api;

import it.univr.track.controller.api.dto.CreateShipmentRequest;
import it.univr.track.controller.api.dto.UpdateShipmentRequest;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.helper.Thrower;
import it.univr.track.service.DeviceAssignmentService;
import it.univr.track.service.ShipmentService;
import it.univr.track.service.TrackableService;
import it.univr.track.service.UnitLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Set;

@RestController
public class ShipmentController {

    private final UnitLoadService unitLoadService;
    private final ShipmentService shipmentService;
    private final TrackableService trackableService;
    private final DeviceAssignmentService deviceAssignmentService;

    public ShipmentController(UnitLoadService unitLoadService, ShipmentService shipmentService,
                              TrackableService trackableService, DeviceAssignmentService deviceAssignmentService){
        this.unitLoadService = unitLoadService;
        this.shipmentService = shipmentService;
        this.trackableService = trackableService;
        this.deviceAssignmentService = deviceAssignmentService;
    }
    // create a shipment
    @PostMapping("/shipment")
    public boolean createShipment(@RequestBody CreateShipmentRequest request) {
        try {
            Thrower.notNullArgumentException(request, "request is null");

            Set<Long> trackableIds = request.getTrackableIds();
            Thrower.notNullArgumentException(trackableIds, "trackableIds is null");

            for (Long id : trackableIds) {
                Thrower.notNullArgumentException(id, "trackableId is null");
            }
            Set<UnitLoad> unitLoads = trackableService.getUnitLoadsFromTrackableIds(trackableIds);
            // Controllo che tutte le UnitLoad siano valide prima di creare lo Shipment,
            // una UnitLoad è valida se ha assegnato almeno un Device
            for (UnitLoad ul : unitLoads){
                Thrower.state(!deviceAssignmentService.isTrackableAssignedToDevice(ul),
                        "UnitLoad " + ul.getId() + " is not assigned to a Device");
            }

            shipmentService.create(request.getStartDate(), request.getDescription(), unitLoads);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed" + e.getMessage());
        }
    }

    @PutMapping("/api/shipments/{shipmentId}")
    public boolean updateShipment(@PathVariable Long shipmentId,
                                  @RequestBody UpdateShipmentRequest request) {
        try {
            Result result = getResult(shipmentId, request);
            Set<UnitLoad> unitLoads = trackableService.getUnitLoadsFromTrackableIds(request.getTrackableIds());
            shipmentService.update(result.s(), result.description(), result.endDate(), result.state(),unitLoads);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Update failed");
        }
    }

    private Result getResult(Long shipmentId, UpdateShipmentRequest request) {
        Thrower.notNullArgumentException(request, "request is null");

        Shipment s = shipmentService.getById(shipmentId);

        String description = request.getDescription();
        ZonedDateTime endDate = request.getEndDate();
        ShipmentState state = request.getState();
        Result result = new Result(s, description, endDate, state);
        return result;
    }

    private record Result(Shipment s, String description, ZonedDateTime endDate, ShipmentState state) {
    }


    // read a shipment
    @GetMapping("/shipment/{shipmentId}")
    public Shipment readShipment(@PathVariable Long shipmentId) {
        try {
            return shipmentService.getById(shipmentId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Read failed");
        }
    }


    @PutMapping("/api/shipment")
    public boolean editShipment(@RequestParam UpdateShipmentRequest request) {
        try {
            Thrower.notNullArgumentException(request.getShipmentId(), "shipmentId is null");
            Result result = getResult(request.getShipmentId(), request);
            Set<UnitLoad> unitLoads = trackableService.getUnitLoadsFromTrackableIds(request.getTrackableIds());
            shipmentService.update(result.s(), result.description(), result.endDate(), result.state(),unitLoads);
            return true;
        }  catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Update failed");
        }
    }

    /**
     *
     * @param shipmentId Id of a Shipment
     * @return false mean that does not exist a Shipment of that Id
     */
    // update a shipment
    @DeleteMapping("/api/shipment")
    public boolean deleteShipment(@RequestParam Long shipmentId) {
        try {
            return shipmentService.deleteUnitLoadById(shipmentId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Update failed");
        }
    }

    // ATTACH a trackable (UnitLoad or Package) to shipment
    @PostMapping("/{shipmentId}/trackables/{trackableId}")
    public boolean attach(@PathVariable Long shipmentId, @PathVariable Long trackableId) {
        try {
            shipmentService.attachTrackable(shipmentId, trackableId);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Request rejected");
        }
    }

    // DETACH a trackable (UnitLoad or Package) from shipment
    @DeleteMapping("/{shipmentId}/trackables/{trackableId}")
    public boolean detach(@PathVariable Long shipmentId, @PathVariable Long trackableId) {
        try {
            shipmentService.detachTrackable(shipmentId, trackableId);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Request rejected");
        }
    }

    // list all the shipments
    @GetMapping("/api/shipments")
    public Shipment[] readShipments() {
        return shipmentService.getAllShipments().toArray(new Shipment[0]);
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
