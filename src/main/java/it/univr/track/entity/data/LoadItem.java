package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "unitload_product",
            joinColumns = @JoinColumn(name = "loaditem_id"),
            inverseJoinColumns = @JoinColumn(name = "unitload_id"))
    private Set<UnitLoad> unitLoads;

    @Column(nullable = false)
    private String name; // oppure productLabel

    @Column(nullable = false, unique = true)
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
