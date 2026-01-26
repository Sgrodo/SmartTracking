package it.univr.track.controller.api.dto;

import it.univr.track.entity.enumeration.ShipmentState;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class UpdateShipmentRequest {
    private String description;        // null => non modificare
    private Long shipmentId;
}
