package it.univr.track.entity;

import it.univr.track.entity.data.Trackable;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Questa Classe rappresenta la relazione tra un device ed un Trackable (UnitLoad o Package)
 * Tramite questa entità si ha lo storico dei matching del Device con dei Trackable e si riesce
 * a capire se un Device è in uso oppure no.
 */
@Entity
@Table(name = "device_assignment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeviceAssignment extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Trackable target;

    @Column(name = "from_ts", nullable = false)
    private ZonedDateTime fromTs;

    @Column(name = "to_ts")
    @Check(name = "device_assignment_to_ts_ck", constraints = "to_ts IS NULL or to_ts >= from_ts")
    private ZonedDateTime toTs; // null = attivo

    public DeviceAssignment(Device device, Trackable target) {
        this(device, target, ZonedDateTime.now());
    }

    public DeviceAssignment(Device device, Trackable target, ZonedDateTime fromTs) {
        Thrower.notNullArgumentException(device, "device is null");
        Thrower.notNullArgumentException(target, "target is null");

        this.fromTs = Objects.requireNonNullElseGet(fromTs, ZonedDateTime::now);
        this.device = device;
        this.target = target;
        this.toTs = null;
    }

    public boolean isActive() {
        return toTs == null;
    }

    public void deactivate() {
        deactivate(ZonedDateTime.now());
    }

    public void deactivate(ZonedDateTime toTs) {
        ZonedDateTime effectiveToTs = Objects.requireNonNullElseGet(toTs, ZonedDateTime::now);
        if(!isActive()){
            return;
        }
        this.toTs = effectiveToTs;
    }
}
