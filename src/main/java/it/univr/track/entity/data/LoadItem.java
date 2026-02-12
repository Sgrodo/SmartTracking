package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


/***
 * Questa classe rappresenta l'oggetto trasportato da una UnitLoad, non è StandAlone
 * poichè noi trattiamo Trackable (UnitLoad o Package). Quindi di fatto è un oggetto
 * che viene trasportato all'interno di una UnitLoad.
 */
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LoadItem extends AbstractEntity {

    @ManyToMany(mappedBy = "loadItems")
    private Set<UnitLoad> unitLoads = new HashSet<>();

    @Column(nullable = false)
    private String name; // oppure productLabel

    @Column(nullable = false, unique = true, updatable = false)
    private String qrCode;

    @Column
    private String description;

    public LoadItem(String name, String qrCode, String description) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is blank");
        if (qrCode == null || qrCode.isBlank()) throw new IllegalArgumentException("qrCode is blank");

        this.name = name;
        this.qrCode = qrCode;
        this.description = description;
    }
}
