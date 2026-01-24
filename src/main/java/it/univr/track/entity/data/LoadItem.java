package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


/***
 * Questa classe rappresenta l'oggetto trasportato da una UnitLoad, non è StandAlone
 * poichè noi trattiamo Trackable (UnitLoad o Package). Quindi di fatto è un oggetto
 * che viene trasportato all'interno di una UnitLoad.
 */
@Entity
@Table(name = "unitload_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LoadItem extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "unitload_id", nullable = false)
    private UnitLoad unitLoad;

    @Column(nullable = false)
    private String name; // oppure productLabel

    @Column(nullable = false, unique = true)
    private String qrCode;

    @Column
    private String description;

    public LoadItem(UnitLoad unitLoad, String name, String qrCode, String description) {
        if (unitLoad == null) throw new IllegalArgumentException("unitLoad is null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is blank");
        if (qrCode == null || qrCode.isBlank()) throw new IllegalArgumentException("qrCode is blank");
        this.unitLoad = unitLoad;
        this.name = name;
        this.qrCode = qrCode;
        this.description = description;
    }
}
