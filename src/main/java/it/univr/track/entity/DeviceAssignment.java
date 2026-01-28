package it.univr.track.entity;

import it.univr.track.entity.data.Trackable;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Questa Classe rappresenta la relazione tra un device ed un Trackable (UnitLoad o Package)
 * Tramite questa entità si ha lo storico dei matching del Device con dei Trackable e si riesce
 * a capire se un Device è in uso oppure no.
 */
@Entity
@Table(name = "device_assignment")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // richiesto da JPA
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
    private ZonedDateTime toTs; // null = attivo

    public DeviceAssignment(Device device, Trackable target, ZonedDateTime fromTs) {
        Thrower.notNullArgumentException(device, "device is null");
        Thrower.notNullArgumentException(target, "target is null");
        if (fromTs == null) this.fromTs = ZonedDateTime.now();
        else this.fromTs = fromTs;

        this.device = device;
        this.target = target;
        this.toTs = null;
    }

    public boolean isActive() {
        return toTs == null;
    }

    public void close(ZonedDateTime toTs) {
        if (toTs == null) throw new IllegalArgumentException("toTs is null");
        if (this.toTs != null) throw new IllegalStateException("assignment already closed");
        if (toTs.isBefore(fromTs)) throw new IllegalArgumentException("toTs before fromTs");
        this.toTs = toTs;
    }
}
