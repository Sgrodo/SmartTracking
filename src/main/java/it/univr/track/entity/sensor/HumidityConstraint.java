package it.univr.track.entity.sensor;

import it.univr.track.entity.enumeration.PhysicalConstraintType;

public record HumidityConstraint(double percent) implements PhysicalConstraint {
    public HumidityConstraint {
        if (percent < 0.0 || percent > 100.0)
            throw new IllegalArgumentException("Humidity must be 0..100%");
    }

    @Override public PhysicalConstraintType type() {
        return PhysicalConstraintType.HUMIDITY;
    }
}
