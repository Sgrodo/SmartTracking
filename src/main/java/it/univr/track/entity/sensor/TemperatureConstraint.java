package it.univr.track.entity.sensor;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.entity.enumeration.TemperatureUnit;
import jakarta.validation.constraints.NotNull;

public record TemperatureConstraint(@NotNull Double value, @NotNull TemperatureUnit unit) implements PhysicalConstraint{

    public TemperatureConstraint {
    }

    @Override
    public PhysicalConstraintType type() {
        return PhysicalConstraintType.TEMPERATURE;
    }
}
