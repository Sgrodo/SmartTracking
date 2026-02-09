package it.univr.track.controller.api.dto.trackables;

import it.univr.track.controller.api.dto.trackables.packages.PackageResponse;
import it.univr.track.controller.api.dto.trackables.unitloads.UnitLoadResponse;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TrackableResponse {
    private Long id;

    public TrackableResponse(Trackable trk) {
        this.id = trk.getId();
    }

    public static TrackableResponse fromTrackable(Trackable trk) {
        if (trk instanceof Package) {
            return new PackageResponse((Package) trk);
        } else if (trk instanceof UnitLoad) {
            return new UnitLoadResponse((UnitLoad) trk);
        } else {
            throw new IllegalArgumentException("Unknown trackable type: " + trk.getClass().getName());
        }
    }

}
