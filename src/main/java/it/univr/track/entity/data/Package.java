package it.univr.track.entity.data;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Package rappresenta un contenitore logico di più UnitLoad.
 * Può essere tracciato come Trackable e le informazioni
 * possono essere propagate alle UnitLoad contenute.
 */
@Entity
@Table(name = "package_tbl")
@NoArgsConstructor
@Getter
public class Package extends Trackable {

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitLoad> unitLoads = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    protected void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    /**
     * Metodo di dominio per aggiungere una UnitLoad al Package.
     */
    public void addUnitLoad(UnitLoad unitLoad) {
        if (unitLoad == null) throw new IllegalArgumentException("unitLoad is null");
        unitLoads.add(unitLoad);
        unitLoad.setPackage(this);
    }

    public void removeUnitLoad(UnitLoad unitLoad) {
        if (unitLoad == null) throw new IllegalArgumentException("unitLoad is null");
        unitLoads.remove(unitLoad);
        unitLoad.setPackage(null);
    }
}
