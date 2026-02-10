package it.univr.track.controller.api.dto.devices;

import it.univr.track.entity.Device;
import it.univr.track.entity.enumeration.TrackingState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceResponse {

    private Long id;
    private TrackingState state;
    private boolean isConnected;
    private boolean isCalibrated;
    private String origin;
    private double price;

    public DeviceResponse(Device device) {
        this.id = device.getId();
        this.state = device.getTrackingState();
        this.isConnected = device.getIsConnected();
        this.isCalibrated = device.getIsCalibrated();
        this.origin = device.getOrigin();
        this.price = device.getPrice().doubleValue();
    }

}
