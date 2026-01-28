package it.univr.track.controller.api.dto;

import it.univr.track.entity.enumeration.ShipmentState;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
public class UpdateShipmentRequest {
    private String description;
    private ZonedDateTime endDate;
    private ShipmentState state;
    /***
     * IDs of Trackables (Packages/UnitLoads)
     * if (not present)
     * then to be added to the Shipment
     * else
     * removed from it
     */
    private Set<Long> trackableIds;

    private Long shipmentId;

}
