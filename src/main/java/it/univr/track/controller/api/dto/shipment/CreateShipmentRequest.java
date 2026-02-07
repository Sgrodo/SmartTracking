package it.univr.track.controller.api.dto.shipment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
public class CreateShipmentRequest {

    @NotNull(message = "startDate is required")
    private ZonedDateTime startDate;

    private String description;

    @NotNull(message = "trackableIds is required")
    @NotEmpty(message = "At least one trackable id is needed")
    private Set<@NotNull(message = "trackableId can't be null") Long> trackableIds;
}
