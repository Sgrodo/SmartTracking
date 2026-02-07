package it.univr.track.controller.api.dto.packages;

import java.util.List;

import it.univr.track.controller.api.dto.unitloads.UnitLoadResponse;
import it.univr.track.entity.data.Package;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageResponse {

    private Long id;
    private String groupCode;
    private String description;
    private List<UnitLoadResponse> unitLoads;

    public PackageResponse(Package pkg) {
        this.id = pkg.getId();
        this.groupCode = pkg.getGroupCode();
        this.description = pkg.getDescription();
        this.unitLoads = pkg.getUnitLoads().stream()
                .map(UnitLoadResponse::new)
                .toList();
    }

}
