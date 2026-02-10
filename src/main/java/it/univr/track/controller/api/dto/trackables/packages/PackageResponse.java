package it.univr.track.controller.api.dto.trackables.packages;

import java.util.List;

import it.univr.track.controller.api.dto.devices.DeviceResponse;
import it.univr.track.controller.api.dto.trackables.TrackableResponse;
import it.univr.track.controller.api.dto.trackables.unitloads.UnitLoadResponse;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Trackable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageResponse extends TrackableResponse {

    private String description;
    private String groupCode;
    private List<UnitLoadResponse> unitLoads;
    private List<DeviceResponse> devices;

    public PackageResponse(Package pkg) {
        super(pkg);
        this.description = pkg.getDescription();
        this.groupCode = pkg.getGroupCode();
        this.unitLoads = pkg.getUnitLoads().stream()
                .map(UnitLoadResponse::new)
                .toList();
        this.devices = pkg.getDevices().stream()
                .filter(DeviceAssignment::isActive)
                .map(da -> new DeviceResponse((da.getDevice())))
                .toList();
    }

}
