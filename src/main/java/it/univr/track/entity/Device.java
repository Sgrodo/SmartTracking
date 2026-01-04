package it.univr.track.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
public class Device extends AbstractEntity {

    @Column(nullable = false)
    private Boolean isConnected;

    @Column(nullable = false)
    private Boolean state;

    @Column(nullable = false)
    private Boolean isCalibrated;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Origin / Formula del device (rappresentazione testuale)
     */
    @Column(nullable = false)
    private String origin;

    /**
     * Molti device possono appartenere a una shipment
     */
    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = true)
    private Shipment shipment;

    public Device(
            Boolean isConnected,
            Boolean state,
            Boolean isCalibrated,
            BigDecimal price,
            String origin
    ) {
        if (price == null || price.signum() < 0)
            throw new IllegalArgumentException("Invalid price");
        if (origin == null || origin.isBlank())
            throw new IllegalArgumentException("origin required");

        this.isConnected = isConnected;
        this.state = state;
        this.isCalibrated = isCalibrated;
        this.price = price;
        this.origin = origin;
    }

}
