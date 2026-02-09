package it.univr.track.controller.api.dto.shipment;

import it.univr.track.controller.api.dto.trackables.TrackableResponse;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.enumeration.ShipmentState;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class ShipmentResponse {

    private Long id;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ShipmentState state;
    private Set<TrackableResponse> trackables;

    public ShipmentResponse(Shipment shipment) {
        this.id = shipment.getId();
        this.description = shipment.getDescription();
        this.startDate = shipment.getStartDate();
        this.endDate = shipment.getEndDate();
        this.state = shipment.getState();
        this.trackables = shipment.getTrackables()
                .stream()
                .map(TrackableResponse::fromTrackable)
                .collect(Collectors.toSet());
    }

}
