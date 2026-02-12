package it.univr.track.controller.api.dto.shipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
public class CreateShipmentRequest {

    private ZonedDateTime startDate = ZonedDateTime.now();

    private String description;

    @NotNull(message = "from location is required")
    @NotBlank(message = "from location can't be blank")
    private String from;

    @NotNull(message = "to location is required")
    @NotBlank(message = "to location can't be blank")
    private String to;

    @NotNull(message = "trackableIds is required")
    @NotEmpty(message = "At least one trackable id is needed")
    private Set<@NotNull(message = "trackableId can't be null") Long> trackableIds;
}
