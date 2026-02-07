package it.univr.track.controller.api.dto.shipment;

import it.univr.track.entity.enumeration.ShipmentState;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
public class UpdateShipmentRequest {
    private String description;

    @NotNull(message = "endDate is required")
    private ZonedDateTime endDate;

    @NotNull(message = "state is required")
    private ShipmentState state;

    @NotNull(message = "trackableIds is required")
    @NotEmpty(message = "At least one trackableId is needed")
    private Set<Long> trackableIds;

    @NotNull(message = "shipmentId is required")
    private Long shipmentId;

}
