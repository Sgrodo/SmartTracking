package it.univr.track.entity.sensor;

import it.univr.track.entity.enumeration.PhysicalConstraintType;

public sealed interface PhysicalConstraint
        permits TemperatureConstraint, HumidityConstraint {

    PhysicalConstraintType type();
}
