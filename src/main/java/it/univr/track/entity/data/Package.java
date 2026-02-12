package it.univr.track.entity.data;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Package rappresenta un contenitore logico di più UnitLoad.
 * Può essere tracciato come Trackable e le informazioni
 * possono essere propagate alle UnitLoad contenute.
 */
@Entity
@Table(name = "package_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Package extends Trackable {

    @OneToMany(mappedBy = "pkg", cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    private Set<UnitLoad> unitLoads = new HashSet<>();

    @Setter
    private String description;

    @Setter
    private String groupCode;

    public Package(String description, String groupCode) {
        if(groupCode == null || groupCode.isBlank())
            throw new IllegalArgumentException("groupCode is null or blank");
        this.groupCode = groupCode;
        this.description = Objects.requireNonNullElse(description, "");
    }

    public Package(String groupCode){
        this("", groupCode);
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

    @Override
    public String getDisplayName() {
        return "Package";
    }
}
