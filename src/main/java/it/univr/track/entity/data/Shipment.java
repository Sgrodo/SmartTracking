package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
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
@NoArgsConstructor
@Getter
public class Shipment extends AbstractEntity {

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column
    @Check(name = "shipment_enddate_check", constraints = "endDate == null or endDate >= startDate")
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
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trackable> trackables = new HashSet<>();

    public Shipment(ZonedDateTime startDate, String description, Collection<Trackable> trackables) {
        this(startDate, description);
        this.trackables = new HashSet<>(trackables);
    }

    public Shipment(ZonedDateTime startDate, String description) {
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
            for (UnitLoad ut : t.getUnitLoads()){
                if (visited.contains(ut)) {
                    throw new IllegalStateException("Trackable " + ut.getId() + " is assigned to multiple Trackables in the same Shipment");
                }
                visited.add(ut);
            }
            if (t.getShipment() != null && !t.getShipment().equals(this)){
                throw new IllegalStateException(String.format("Trying to add trackable[%s] assigned to another shipment[%s]: ", t.getId(), t.getShipment().getId()));
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

    public void setInTransit(){
        this.setInTransit(ZonedDateTime.now());
    }

    public void setInTransit(ZonedDateTime now) {
        if(this.state.equals(ShipmentState.IN_TRANSIT)){
            return;
        }

        this.state = ShipmentState.IN_TRANSIT;
        this.endDate = null;
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
