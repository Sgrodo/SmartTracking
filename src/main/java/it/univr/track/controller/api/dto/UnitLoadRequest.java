package it.univr.track.controller.api.dto;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.entity.sensor.PhysicalConstraint;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class UnitLoadRequest {

    // getter & setter (o Lombok @Getter @Setter)
    private String description;
    private ZonedDateTime startDate;
    private Set<PhysicalConstraint> constraints;
    private List<Long> loadItems;

}
