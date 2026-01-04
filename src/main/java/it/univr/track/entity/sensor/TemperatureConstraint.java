package it.univr.track.entity.sensor;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.entity.enumeration.TemperatureUnit;

public record TemperatureConstraint(double value, TemperatureUnit unit) implements PhysicalConstraint{

    public TemperatureConstraint {
        if (unit == null) unit = TemperatureUnit.CELSIUS;
    }

    @Override
    public PhysicalConstraintType type() {
        return PhysicalConstraintType.TEMPERATURE;
    }
}
