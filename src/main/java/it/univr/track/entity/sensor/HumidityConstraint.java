package it.univr.track.entity.sensor;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.exception.InvalidDataException;
import jakarta.validation.constraints.NotNull;

public record HumidityConstraint(@NotNull Double percent) implements PhysicalConstraint {
    public HumidityConstraint {
        if (percent < 0.0 || percent > 100.0)
            throw new InvalidDataException("Humidity must be 0..100%");
    }

    @Override
    public PhysicalConstraintType type() {
        return PhysicalConstraintType.HUMIDITY;
    }
}
