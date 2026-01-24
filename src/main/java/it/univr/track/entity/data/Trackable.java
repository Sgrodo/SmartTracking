package it.univr.track.entity.data;

import it.univr.track.entity.AbstractEntity;
import jakarta.persistence.*;
        import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "trackable")
@NoArgsConstructor
@Getter
/***
 * Questa classe Entity serve per gestire il binding dei device a unitloads e packages in modo univoco
 */
public abstract class Trackable extends AbstractEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "target_id") // FK in sending
    private List<Sending> sendings = new ArrayList<>();

    public void addSending(Sending sending) {
        if (sending == null) throw new IllegalArgumentException("sending is null");
        sendings.add(sending);
    }
}
