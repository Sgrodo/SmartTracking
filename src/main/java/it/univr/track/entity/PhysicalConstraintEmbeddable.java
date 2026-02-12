package it.univr.track.entity;

import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.entity.enumeration.TemperatureUnit;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.entity.sensor.TemperatureConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
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

    public static PhysicalConstraintEmbeddable fromDomain(PhysicalConstraint c) {
        var e = new PhysicalConstraintEmbeddable();
        e.type = c.type();

        if (c instanceof HumidityConstraint h) {
            e.numberValue = h.percent();
            e.temperatureUnit = null;

        } else if (c instanceof TemperatureConstraint t) {
            e.numberValue = t.value();
            e.temperatureUnit = t.unit();

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

}
