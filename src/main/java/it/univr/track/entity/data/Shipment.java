package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Shipment rappresenta la spedizione/viaggio logistico (contenitore).
 * Non contiene dati di tracking: quelli stanno su Trackable (Package/UnitLoad).
 */
@Entity
@Table(name = "shipment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Shipment extends AbstractEntity {

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column
    @Check(name = "shipment_enddate_check", constraints = "end_date IS NULL or end_date >= start_date")
    @Check(
            name = "shipment_state_enddate_check",
            constraints = """
                (
                    (state IN ('COMPLETED', 'CANCELLED') AND end_date IS NOT NULL)
                    OR
                    (state NOT IN ('COMPLETED', 'CANCELLED') AND end_date IS NULL)
                )
            """
    )
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
    @OneToMany(mappedBy = "shipment")
    private Set<Trackable> trackables = new HashSet<>();

    public Shipment(ZonedDateTime startDate, String description) {
        this.startDate = Objects.requireNonNullElse(startDate, ZonedDateTime.now());
        this.description = description;
    }

    public Shipment(String description) {
        this(ZonedDateTime.now(), description);
    }

    // DOMAIN METHODS
    public void addTrackable(Trackable trackable) {
        Thrower.notNullArgumentException(trackable, "UnitLoad is null");
        if (trackable.getShipment() != null && !trackable.getShipment().equals(this)){
            throw new IllegalStateException(String.format("Trying to add trackable[%s] assigned to another shipment[%s]: ", trackable.getId(), trackable.getShipment().getId()));
        }

        trackables.add(trackable);
        trackable.setShipment(this);
    }

    public void removeTrackable(Trackable trackable) {
        Thrower.notNullArgumentException(trackable, "UnitLoad is null");
        trackables.remove(trackable);
        trackable.setShipment(null);
    }

    /**
     * Validazione dello stato della Shipment prima di salvarla
     * Coerenza tra stato e date
     */
    @PrePersist
    @PreUpdate
    private void validate() {
        this.validateTrackables();
    }

    private void validateTrackables() {
        Set<Trackable> visited = new HashSet<>();
        for (Trackable t : trackables) {
            // Uniqueness among trackables in the same shipment
            for (UnitLoad ut : t.getUnitLoads()){
                if (visited.contains(ut)) {
                    throw new InvalidDataException("Trackable " + ut.getId() + " is assigned to multiple Trackables in the same Shipment");
                }
                visited.add(ut);
            }
        }
    }

    private boolean isFinalState() {
        return state.equals(ShipmentState.COMPLETED) || state.equals(ShipmentState.CANCELLED);
    }

    public void setState(ShipmentState state) {
        this.setState(state, ZonedDateTime.now());
    }

    public void setState(ShipmentState state, ZonedDateTime time) {
        if (state == null) throw new IllegalArgumentException("state is null");
        switch (state){
            case COMPLETED -> this.complete(time);
            case CANCELLED -> this.cancel(time);
            case IN_TRANSIT -> this.setInTransit(time);
            case CREATED -> {
                this.startDate = ZonedDateTime.now();
                this.endDate = null;
                this.state = ShipmentState.CREATED;
            }
            default -> throw new IllegalArgumentException("Unsupported state: " + state);
        }
    }

    public void complete() {
        this.complete(ZonedDateTime.now());
    }

    public void complete(ZonedDateTime now) {
        ZonedDateTime zonedDateTime = now == null ? now = ZonedDateTime.now() : now;
        if (this.isFinalState()) {
            throw new IllegalStateException("Cannot change state of a final shipment");
        }

        this.endDate = zonedDateTime;
        this.state = ShipmentState.COMPLETED;
    }

    public void cancel() {
        this.cancel(ZonedDateTime.now());
    }

    public void cancel(ZonedDateTime now) {
        ZonedDateTime zonedDateTime = now == null ? now = ZonedDateTime.now() : now;

        if (this.isFinalState()) {
            throw new IllegalStateException("Cannot change state of a final shipment");
        }

        this.endDate = zonedDateTime;
        this.state = ShipmentState.CANCELLED;
    }

    public void setInTransit(){
        this.setInTransit(ZonedDateTime.now());
    }

    public void setInTransit(ZonedDateTime now) {
        if(this.isFinalState()) {
            throw new IllegalStateException("Cannot change state of a final shipment");
        }

        this.state = ShipmentState.IN_TRANSIT;
        this.endDate = null;
    }

}
