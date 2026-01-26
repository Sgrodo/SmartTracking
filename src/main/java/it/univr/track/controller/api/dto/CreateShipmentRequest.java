package it.univr.track.controller.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CreateShipmentRequest {
    private ZonedDateTime startDate;
    private String description;
}
