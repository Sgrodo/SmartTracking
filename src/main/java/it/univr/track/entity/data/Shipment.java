package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import it.univr.track.entity.enumeration.ShipmentState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
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
    private Set<Package> packages = new HashSet<>();

    // Shipment -> UnitLoads "sfuse" (senza package)
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitLoad> unitLoads = new HashSet<>();

    public Shipment(ZonedDateTime startDate, String description) {
        if (startDate == null) throw new IllegalArgumentException("startDate is required");
        this.startDate = startDate;
        this.description = description;
    }

    // DOMAIN METHODS

    public void addPackage(Package pkg) {
        if (pkg == null) throw new IllegalArgumentException("package is null");
        packages.add(pkg);
        pkg.setShipment(this);
    }

    public void removePackage(Package pkg) {
        if (pkg == null) throw new IllegalArgumentException("package is null");
        packages.remove(pkg);
        pkg.setShipment(null);
    }

    public void addUnitLoad(UnitLoad ul) {
        if (ul == null) throw new IllegalArgumentException("unitLoad is null");
        unitLoads.add(ul);
        ul.setShipment(this);
    }

    public void removeUnitLoad(UnitLoad ul) {
        if (ul == null) throw new IllegalArgumentException("unitLoad is null");
        unitLoads.remove(ul);
        ul.setShipment(null);
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
