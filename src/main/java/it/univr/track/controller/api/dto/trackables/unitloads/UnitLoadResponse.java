package it.univr.track.controller.api.dto.trackables.unitloads;

import it.univr.track.controller.api.dto.devices.DeviceResponse;
import it.univr.track.controller.api.dto.trackables.TrackableResponse;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.UnitLoad;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class UnitLoadResponse extends TrackableResponse {

    private String description;
    private ZonedDateTime startDate;
    private String pkgId;
    private List<LoadItemResponse> loadItems;
    private List<DeviceResponse> devices;

    public UnitLoadResponse(UnitLoad unitLoad) {
        super(unitLoad);
        this.description = unitLoad.getDescription();
        this.startDate = unitLoad.getStartDate();
        this.pkgId = unitLoad.getPkg() != null ? unitLoad.getPkg().getId().toString() : null;
        this.loadItems = unitLoad.getLoadItems().stream()
                .map(LoadItemResponse::new)
                .toList();
        this.devices = unitLoad.getDevices().stream()
                .filter(DeviceAssignment::isActive)
                .map((assignment) -> new DeviceResponse(assignment.getDevice()))
                .toList();
    }

}
