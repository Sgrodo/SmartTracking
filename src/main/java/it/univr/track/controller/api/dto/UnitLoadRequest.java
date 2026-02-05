package it.univr.track.controller.api.dto;

import it.univr.track.entity.sensor.PhysicalConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UnitLoadRequest {

    private String description;

    private ZonedDateTime startDate = ZonedDateTime.now();

    private Set<@NotNull(message = "constraint can't be null") PhysicalConstraint> constraints;

    @NotNull(message = "loadItems is required")
    @NotEmpty(message = "At least one loadItem is needed")
    private List<@NotNull(message = "loadItemId can't be null") Long> loadItemIds;

}
