package it.univr.track.controller.api.dto.unitloads;

import it.univr.track.entity.data.UnitLoad;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class UnitLoadResponse {

    private Long id;
    private ZonedDateTime startDate;
    private String description;
    private String pkgId;
    private List<LoadItemResponse> loadItems;

    public UnitLoadResponse(UnitLoad unitLoad) {
        this.id = unitLoad.getId();
        this.startDate = unitLoad.getStartDate();
        this.description = unitLoad.getDescription();
        this.pkgId = unitLoad.getPkg() != null ? unitLoad.getPkg().getId().toString() : null;
        this.loadItems = unitLoad.getLoadItems().stream()
                .map(LoadItemResponse::new)
                .toList();
    }

}
