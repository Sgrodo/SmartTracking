package it.univr.track.entity;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.entity.enumeration.TemperatureUnit;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.entity.sensor.TemperatureConstraint;
import jakarta.persistence.*;

@Embeddable
public class PhysicalConstraintEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhysicalConstraintType type;

    // campi generici (ne usi alcuni a seconda del type)
    @Column
    private Double numberValue;

    @Enumerated(EnumType.STRING)
    @Column
    private TemperatureUnit temperatureUnit;

    protected PhysicalConstraintEmbeddable() {} // JPA

    // factory: dominio -> embeddable
    public static PhysicalConstraintEmbeddable fromDomain(PhysicalConstraint c) {
        var e = new PhysicalConstraintEmbeddable();
        e.type = c.type();

        if (c instanceof HumidityConstraint h) {
            e.numberValue = h.percent();
            e.temperatureUnit = null;

        } else if (c instanceof TemperatureConstraint t) {
            e.numberValue = t.value();
            e.temperatureUnit = t.unit();

        } else {
            throw new IllegalArgumentException(
                    "Unsupported PhysicalConstraint type: " + c.getClass()
            );
        }
        return e;
    }


    // factory: embeddable -> dominio
    public PhysicalConstraint toDomain() {
        return switch (type) {
            case HUMIDITY -> new HumidityConstraint(numberValue);
            case TEMPERATURE -> new TemperatureConstraint(numberValue, temperatureUnit);
        };
    }

    // IMPORTANTISSIMO: equals/hashCode "a valore"
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PhysicalConstraintEmbeddable other)) return false;
        return type == other.type
                && java.util.Objects.equals(numberValue, other.numberValue)
                && temperatureUnit == other.temperatureUnit;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, numberValue, temperatureUnit);
    }
}
