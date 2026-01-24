package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import it.univr.track.entity.Device;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "sending")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Sending extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private ZonedDateTime timestamp;

    @Column(nullable = false)
    private boolean secureChannel;

    @Column(nullable = false)
    private boolean dataIntegrity;

    public Sending(Device device, ZonedDateTime timestamp, boolean secureChannel, boolean dataIntegrity) {
        if (device == null) throw new IllegalArgumentException("device is null");
        if (timestamp == null) throw new IllegalArgumentException("timestamp is null");
        this.device = device;
        this.timestamp = timestamp;
        this.secureChannel = secureChannel;
        this.dataIntegrity = dataIntegrity;
    }
}
