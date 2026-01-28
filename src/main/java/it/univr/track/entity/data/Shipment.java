package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Shipment rappresenta la spedizione/viaggio logistico (contenitore).
 * Non contiene dati di tracking: quelli stanno su Trackable (Package/UnitLoad).
 */
@Entity
@Table(name = "shipment")
@NoArgsConstructor
@Getter
public class Shipment extends AbstractEntity {

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column
    private ZonedDateTime endDate;

    @Column
    private String description;

    //Stato (non mandatory ma utile)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentState state = ShipmentState.CREATED;

    // Shipment -> Packages
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trackable> trackables = new HashSet<>();

    public Shipment(ZonedDateTime startDate, String description, Set<Trackable> trackables) {
        this.startDate = Objects.requireNonNullElse(startDate, ZonedDateTime.now());
        this.description = description;
    }

    // DOMAIN METHODS


    public void addTrackable(Trackable trackable) {
        Thrower.notNull(trackable, "UnitLoad is null");
        trackables.add(trackable);
        trackable.setShipment(this);
    }

    public void removeTrackable(Trackable trackable) {
        Thrower.notNull(trackable, "UnitLoad is null");
        trackables.remove(trackable);
        trackable.setShipment(null);
    }

    public void markInTransit() {
        this.state = ShipmentState.IN_TRANSIT;
    }

    public void markComplete(ZonedDateTime endDate) {
        this.endDate = endDate;
        this.state = ShipmentState.COMPLETED;
    }

    public void edit(String description, ShipmentState state, ZonedDateTime endDate) {
        if (description != null) this.description = description;
        if (state != null) this.state = state;
        if (endDate != null) this.endDate = endDate;
    }

}
