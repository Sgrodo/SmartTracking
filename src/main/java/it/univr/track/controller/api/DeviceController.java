package it.univr.track.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import it.univr.track.entity.*;

import java.math.BigDecimal;

@Controller
public class DeviceController {

    // add new device
    @PostMapping("/api/device")
    public boolean addDevice() {
        return true;
    }

    // read the device configuration
    @GetMapping("/api/device/{deviceId}")
    public Device readDeviceConfig(@PathVariable("deviceId") Long id) {
        return new Device(true,true,true,new BigDecimal("1.0"),"origin");
    }

    // update device configuration
    @PutMapping("/api/device")
    public boolean editDevice() {
        return true;
    }

    // decommission a device
    @DeleteMapping("/api/device")
    public boolean deleteDevice() {
        return true;
    }

    // list all the devices that are visible for this user
    @GetMapping("/api/devices")
    public Device[] devices() {
        return new Device[0];
    }


}