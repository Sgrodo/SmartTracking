package it.univr.track.entity;

import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.TrackingState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
public class Device extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingState trackingState = TrackingState.INACTIVE;

    @Column(nullable = false)
    private Boolean isConnected = false;

    @Column(nullable = false)
    private Boolean state;

    @Column(nullable = false)
    private Boolean isCalibrated;

    @Check(name = "device_price_positive_ck", constraints = "price >= 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Origin / Formula del device (rappresentazione testuale)
     */
    @Check(name = "device_origin_not_empty_ck", constraints = "origin <> ''")
    @Column(nullable = false)
    private String origin;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "device", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private Set<DeviceAssignment> deviceAssignments;


    public Device(Boolean isConnected, Boolean state, Boolean isCalibrated, BigDecimal price, String origin) {
        this.isConnected = isConnected;
        this.state = state;
        this.isCalibrated = isCalibrated;
        this.price = price;
        this.origin = origin;
        this.trackingState = TrackingState.INACTIVE;
    }

}
