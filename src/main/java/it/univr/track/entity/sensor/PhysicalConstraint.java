package it.univr.track.entity.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.univr.track.entity.enumeration.PhysicalConstraintType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HumidityConstraint.class, name = "HUMIDITY"),
        @JsonSubTypes.Type(value = TemperatureConstraint.class, name = "TEMPERATURE")
})
public sealed interface PhysicalConstraint
        permits TemperatureConstraint, HumidityConstraint {

    PhysicalConstraintType type();
}
