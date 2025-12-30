package it.univr.track.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//TODO changed endpoints, is it OK? (//list devices)
@Controller
public class ShipmentWebController {

    //create new shipment
    @RequestMapping("/web/newShipment")
    public String newShipment() {
        return "newShipment";
    }

    //list shipments
    @RequestMapping("/web/shipments/list")
    public String shipments() {
        return "shipments";
    }

    //activate/deactivate tracking
    @RequestMapping("/web/tracking")
    public String tracking() {
        return "tracking";
    }

    //allocate a device to a shipment
    @RequestMapping("/web/shipmentAllocate")
    public String shipmentAllocate() {
        return "shipmentAllocate";
    }

}
