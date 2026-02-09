package it.univr.track.entity.data;

import it.univr.track.entity.PhysicalConstraintEmbeddable;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.helper.Thrower;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Table(name = "unit_load")
@Getter
public class UnitLoad extends Trackable {

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private Package pkg;


    public void setPackage(Package pkg) {
        this.pkg = pkg;
    }


    // CONTENT (LoadItem)

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "unitload_product",
            joinColumns = @JoinColumn(name = "unitload_id"),
            inverseJoinColumns = @JoinColumn(name = "loaditem_id")
    )
    private Set<LoadItem> loadItems = new HashSet<>();

    // CONSTRAINTS

    @ElementCollection
    @CollectionTable(
            name = "unitload_physical_constraints",
            joinColumns = @JoinColumn(name = "unitload_id")
    )
    private Set<PhysicalConstraintEmbeddable> physicalConstraints = new HashSet<>();

    public UnitLoad(String description, ZonedDateTime startDate, Collection<PhysicalConstraint> constraints) {
        this.startDate = Objects.requireNonNullElse(startDate, ZonedDateTime.now());
        this.description = Objects.requireNonNullElse(description, "");

        if (constraints != null) {
            for (PhysicalConstraint c : constraints) {
                this.physicalConstraints.add(PhysicalConstraintEmbeddable.fromDomain(c));
            }
        }else{
            this.physicalConstraints = new HashSet<>();
        }
    }

    public UnitLoad(ZonedDateTime startDate) {
        this("", startDate, null);
    }

    public UnitLoad() {
        this(ZonedDateTime.now());
    }

    // -------- PRODUCTS SECTION --------

    /**
     * Crea e aggiunge un LoadItem come contenuto di questa UnitLoad.
     * LoadItem non è standalone nel dominio: nasce con UnitLoad.
     */
    public LoadItem addLoadItem(LoadItem loadItem) {
        Thrower.notNullArgumentException(loadItem, "loadItem is required");
        loadItems.add(loadItem);
        loadItem.getUnitLoads().add(this);
        return loadItem;
    }

    public boolean removeProductByQrCode(String qrCode) {
        if (qrCode == null || qrCode.isBlank()) throw new IllegalArgumentException("qrCode is blank");
        return loadItems.removeIf(p -> qrCode.equals(p.getQrCode()));
    }

    public boolean hasProducts() {
        return !loadItems.isEmpty();
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

    /**
     * non puoi attivare se non hai almeno un product.
     * (Se vuoi la metti nel service invece che qui)
     */


    @Override
    public Set<UnitLoad> getUnitLoads() {
        return Set.of(this);
    }

}
