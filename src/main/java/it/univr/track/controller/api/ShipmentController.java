package it.univr.track.controller.api;

import it.univr.track.controller.api.dto.shipment.CreateShipmentRequest;
import it.univr.track.controller.api.dto.shipment.ShipmentResponse;
import it.univr.track.controller.api.dto.shipment.UpdateShipmentRequest;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.service.DeviceAssignmentService;
import it.univr.track.service.ShipmentService;
import it.univr.track.service.TrackableService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Set;

@Validated
@RestController
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final TrackableService trackableService;
    private final DeviceAssignmentService deviceAssignmentService;

    public ShipmentController(ShipmentService shipmentService,
                              TrackableService trackableService, DeviceAssignmentService deviceAssignmentService) {
        this.shipmentService = shipmentService;
        this.trackableService = trackableService;
        this.deviceAssignmentService = deviceAssignmentService;
    }

    // create a shipment
    @PostMapping("/shipment")
    public boolean createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        Set<Long> trackableIds = request.getTrackableIds();

        Set<Trackable> trackables = trackableService.getAllTrackablesById(trackableIds);
        shipmentService.create(request.getStartDate(), request.getDescription(), trackables);

        return true;
    }


    // read a shipment
    @GetMapping("/shipment/{shipmentId}")
    public ShipmentResponse readShipment(@PathVariable Long shipmentId) {
        return new ShipmentResponse(shipmentService.getById(shipmentId));
    }


    @PutMapping("/api/shipment")
    public boolean editShipment(@Valid @RequestBody UpdateShipmentRequest request) {
        Shipment shipment = shipmentService.getById(request.getShipmentId());
        String description = request.getDescription();
        ZonedDateTime endDate = request.getEndDate();
        ShipmentState state = request.getState();
        Set<Trackable> allTrackablesById = trackableService.getAllTrackablesById(request.getTrackableIds());
        shipmentService.update(shipment, description, endDate, state, allTrackablesById);
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
        return shipmentService.deleteById(shipmentId);
    }

    // list all the shipments
    @GetMapping("/api/shipments")
    public ShipmentResponse[] readShipments() {
        return shipmentService.getAllShipments().stream().map(ShipmentResponse::new).toList().toArray(new ShipmentResponse[0]);
    }

    // start/stop tracking
    @PostMapping("/api/shipment/tracking")
    public boolean setShipmentTracking(@RequestParam Long shipmentId, @RequestParam boolean state) {
        TrackingState tState = state ? TrackingState.ACTIVE : TrackingState.INACTIVE;

        shipmentService.setTrackingState(shipmentId, tState);
        return true;
    }

    // allocate a device to a shipment
    @PostMapping("/api/shipment/allocate")
    public boolean allocateDevice(@RequestParam Long trackableId, @RequestParam Long deviceId) {
        trackableService.getById(trackableId); // solo per validare che esista
        deviceAssignmentService.bind(deviceId, trackableId);
        return true;
    }


    // deallocate a device to a shipment
    @PostMapping("/api/shipment/deallocate")
    public boolean deallocateDevice(@RequestParam Long deviceId) {
        deviceAssignmentService.unbind(deviceId);
        return true;
    }

}
