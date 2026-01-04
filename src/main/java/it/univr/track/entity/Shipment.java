package it.univr.track.entity;

import it.univr.track.entity.data.Sending;
import it.univr.track.entity.sensor.PhysicalConstraint;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "shipments")
@NoArgsConstructor
@Getter
public class Shipment extends AbstractEntity {

    @Column(nullable = false)
    private ZonedDateTime startDate;

    // decidi tu se obbligatoria
    @Column
    private String description;

    @OneToMany(mappedBy = "shipment")
    private Set<Device> devices = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "shipment_physical_constraints",
            joinColumns = @JoinColumn(name = "shipment_id")
    )
    private Set<PhysicalConstraintEmbeddable> physicalConstraints = new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL) // niente orphanRemoval
    @JoinColumn(name = "shipment_id")     // FK in sendings
    private List<Sending> sendings = new ArrayList<>();

    public Shipment(String description, ZonedDateTime startDate, Set<PhysicalConstraint> physicalConstraints) {
        if (startDate == null) throw new IllegalArgumentException("startDate is required");
        this.startDate = startDate;
        this.description = description;

        if (physicalConstraints != null) {
            for (PhysicalConstraint c : physicalConstraints){
                this.physicalConstraints.add(PhysicalConstraintEmbeddable.fromDomain(c));
            }
        }
    }

    public void addDevice(Device device) {
        if (device == null) throw new IllegalArgumentException("device is null");
        devices.add(device);
        device.setShipment(this);
    }

    public void removeDevice(Device device) {
        if (device == null) throw new IllegalArgumentException("device is null");
        boolean removed = devices.remove(device);
        if (removed) {
            device.setShipment(null);
        }
    }


    public void addPhysicalConstraint(PhysicalConstraint c) {
        if (c == null) throw new IllegalArgumentException("constraint is null");
        physicalConstraints.add(PhysicalConstraintEmbeddable.fromDomain(c));
    }


    public boolean removePhysicalConstraint(PhysicalConstraint constraint) {
        if (constraint == null) throw new IllegalArgumentException("constraint is null");
        return physicalConstraints.remove(PhysicalConstraintEmbeddable.fromDomain(constraint));
    }


    public void editPhysicalConstraint(PhysicalConstraint oldConstraint, PhysicalConstraint newConstraint) {
        if (oldConstraint == null) throw new IllegalArgumentException("oldConstraint is null");
        if (newConstraint == null) throw new IllegalArgumentException("newConstraint is null");

        boolean removed = physicalConstraints.remove(PhysicalConstraintEmbeddable.fromDomain(oldConstraint));
        if (!removed) {
            throw new IllegalArgumentException("Constraint to edit not found: " + oldConstraint);
        }

        physicalConstraints.add(PhysicalConstraintEmbeddable.fromDomain(newConstraint));
    }


    public void addSending(Sending sending) {
        if (sending == null) throw new IllegalArgumentException("sending is null");
        sendings.add(sending);
    }
}
