package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @Column
    private String description;

    //Stato (non mandatory ma utile)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentState state = ShipmentState.CREATED;

    // Shipment -> Packages
    @Setter
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trackable> trackables = new HashSet<>();

    public Shipment(ZonedDateTime startDate, String description, Set<Trackable> trackables) {
        this.startDate = Objects.requireNonNullElse(startDate, ZonedDateTime.now());
        this.description = description;
    }

    // DOMAIN METHODS


    public void addTrackable(Trackable trackable) {
        Thrower.notNullArgumentException(trackable, "UnitLoad is null");
        trackables.add(trackable);
        trackable.setShipment(this);
    }

    public void removeTrackable(Trackable trackable) {
        Thrower.notNullArgumentException(trackable, "UnitLoad is null");
        trackables.remove(trackable);
        trackable.setShipment(null);
    }

    @PrePersist
    @PreUpdate
    /***
     * Validazione dello stato della Shipment prima di salvarla
     * Coerenza tra stato e date
     */
    private void validate() {
        if (this.isFinalState() && endDate == null) {
            throw new IllegalStateException("Shipment with state " + state + " must have an endDate");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalStateException("endDate cannot be before startDate");
        }

        if(endDate != null && !this.isFinalState()){
            // se ha una endDate allora lo stato non deve essere "finale"
            throw new IllegalStateException("Shipment with endDate must be in COMPLETED or CANCELLED state");
        }

        this.validateTrackables();
    }

    private void validateTrackables() {
        Set<Trackable> visited = new HashSet<>();
        for (Trackable t : trackables) {
            for (UnitLoad ut : t.getUnitLoads()){
                if (visited.contains(ut)) {
                    throw new IllegalStateException("Trackable " + ut.getId() + " is assigned to multiple Trackables in the same Shipment");
                }
                visited.add(ut);
            }
        }
    }

    private boolean isFinalState() {
        return state.equals(ShipmentState.COMPLETED) || state.equals(ShipmentState.CANCELLED);
    }

    public void complete() {
        this.complete(ZonedDateTime.now());
    }

    public void complete(ZonedDateTime now) {
        if (this.isFinalState() && this.endDate != null) {
            return;
        }

        this.endDate = now;
        this.state = ShipmentState.COMPLETED;
    }

    public void cancel() {
        this.cancel(ZonedDateTime.now());
    }

    public void cancel(ZonedDateTime now) {
        if (this.isFinalState() && this.endDate != null) {
            return;
        }

        this.endDate = now;
        this.state = ShipmentState.CANCELLED;
    }

    public Set<UnitLoad> getUnitLoads() {
        Set<UnitLoad> unitLoads = new HashSet<>();
        for (Trackable t : trackables) {
            if (t instanceof UnitLoad ul) {
                unitLoads.add(ul);
            }
        }
        return unitLoads;
    }

}
