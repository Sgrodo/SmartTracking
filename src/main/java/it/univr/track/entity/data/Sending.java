package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "sendings")
@Getter
@Setter
@NoArgsConstructor
public class Sending extends AbstractEntity {

    @Column(nullable = false)
    private Long deviceId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private Boolean secureChannel;

    @Column(nullable = false)
    private Boolean dataIntegrity;

    public Sending(Long deviceId, Instant timestamp, Boolean secureChannel, Boolean dataIntegrity) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.secureChannel = secureChannel;
        this.dataIntegrity = dataIntegrity;
    }
}

