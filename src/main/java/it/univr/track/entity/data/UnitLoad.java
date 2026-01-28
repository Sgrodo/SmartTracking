package it.univr.track.entity.data;

import it.univr.track.entity.PhysicalConstraintEmbeddable;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Table(name = "unit_load")
@NoArgsConstructor
@Getter
public class UnitLoad extends Trackable {

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingState trackingState = TrackingState.INACTIVE;

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private Package pkg;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;


    protected void setPackage(Package pkg) {
        this.pkg = pkg;
    }


    // CONTENT (LoadItem)

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "unitload_product",
            joinColumns = @JoinColumn(name = "unitload_id"),
            inverseJoinColumns = @JoinColumn(name = "loaditem_id")
    )
    private Set<LoadItem> products = new HashSet<>();

    // CONSTRAINTS

    @ElementCollection
    @CollectionTable(
            name = "unitload_physical_constraints",
            joinColumns = @JoinColumn(name = "unitload_id")
    )
    private Set<PhysicalConstraintEmbeddable> physicalConstraints = new HashSet<>();

    public UnitLoad(String description, ZonedDateTime startDate, Set<PhysicalConstraint> constraints, List<LoadItem> loadItems) {
        Thrower.notNullArgumentException(startDate, "startDate is required");
        this.startDate = startDate;
        this.description = description;

        if (constraints != null) {
            for (PhysicalConstraint c : constraints) {
                this.physicalConstraints.add(PhysicalConstraintEmbeddable.fromDomain(c));
            }
        }
    }

    // -------- PRODUCTS SECTION --------

    /**
     * Crea e aggiunge un LoadItem come contenuto di questa UnitLoad.
     * LoadItem non è standalone nel dominio: nasce con UnitLoad.
     */
    public LoadItem addProduct(String name, String qrCode, String description) {
        LoadItem p = new LoadItem(name, qrCode, description);

        products.add(p);

        return p;
    }

    public boolean removeProductByQrCode(String qrCode) {
        if (qrCode == null || qrCode.isBlank()) throw new IllegalArgumentException("qrCode is blank");
        return products.removeIf(p -> qrCode.equals(p.getQrCode()));
    }

    public boolean hasProducts() {
        return !products.isEmpty();
    }

    // -------- CONSTRAINTS SECTION --------

    public void addPhysicalConstraint(PhysicalConstraint c) {
        Thrower.notNullArgumentException(c, "constraint is null");
        physicalConstraints.add(PhysicalConstraintEmbeddable.fromDomain(c));
    }

    public boolean removePhysicalConstraint(PhysicalConstraint constraint) {
        Thrower.notNullArgumentException(constraint, "constraint is null");
        return physicalConstraints.remove(PhysicalConstraintEmbeddable.fromDomain(constraint));
    }

    public void editPhysicalConstraint(PhysicalConstraint oldConstraint, PhysicalConstraint newConstraint) {
        Thrower.notNullArgumentException(oldConstraint, "oldConstraint is null");
        Thrower.notNullArgumentException(newConstraint, "newConstraint is null");

        boolean removed = physicalConstraints.remove(PhysicalConstraintEmbeddable.fromDomain(oldConstraint));
        if (!removed) throw new IllegalArgumentException("Constraint to edit not found: " + oldConstraint);

        physicalConstraints.add(PhysicalConstraintEmbeddable.fromDomain(newConstraint));
    }

    /**
     * non puoi attivare se non hai almeno un product.
     * (Se vuoi la metti nel service invece che qui)
     */
    public void activateTracking() {
        Thrower.state(products.isEmpty(), "UnitLoad must contain at least one product before activation");
        this.trackingState = TrackingState.ACTIVE;
    }

    public void deactivateTracking(){
        this.trackingState = TrackingState.INACTIVE;
    }

    @Override
    public Set<UnitLoad> getUnitLoads() {
        return Set.of(this);
    }
}
