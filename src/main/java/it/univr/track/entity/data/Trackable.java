package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import it.univr.track.entity.DeviceAssignment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***
 * Questa classe Entity serve per gestire il binding dei device a unitloads e packages in modo univoco
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "trackable")
@NoArgsConstructor
@Getter
public abstract class Trackable extends AbstractEntity {

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    @JoinColumn(name = "target_id") // FK in sending
    private List<Sending> sendings = new ArrayList<>();

    @OneToMany(mappedBy = "target", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private Set<DeviceAssignment> devices = new HashSet<>();


    @Setter
    @ManyToOne()
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;


    public abstract Set<UnitLoad> getUnitLoads();
    
    public abstract String getDisplayName();
}
