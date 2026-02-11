/*
package it.univr.track.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//TODO changed endpoints, is it OK? (//list devices)
*/
package it.univr.track.controller.web;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.enumeration.PhysicalConstraintType;
import it.univr.track.entity.enumeration.TemperatureUnit;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.entity.sensor.TemperatureConstraint;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.repository.LoadItemRepository;
import it.univr.track.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class ShipmentWebController {


    private final ShipmentService shipmentService;
    private final UnitLoadService unitLoadService;
    private final PackageService packageService;
    private final DeviceService deviceService;
    private final TrackableService trackableService;
    private final DeviceAssignmentService deviceAssignmentService;
    private final LoadItemRepository loadItemRepository;

   //SHIPMENTS

    private void addBaseShipmentsModel(Model model){
        model.addAttribute("shipmentsActive", shipmentService.getAllNotFinalShipments());
        model.addAttribute("shipmentsClosed", shipmentService.getAllFinalShipments());
    }


    // LIST SHIPMENTS
    @GetMapping("/shipments")
    public String redirectShipments(Model model){
        return "redirect:/web/shipments/list";
    }

    @GetMapping("/shipments/list")
    public String shipments(Model model) {
        this.addBaseShipmentsModel(model);
        return "shipments";
    }

    // NEW SHIPMENT (PAGE)
    @GetMapping("/newShipment")
    public String newShipment(Model model) {
        model.addAttribute("unitLoads", unitLoadService.getAllNotInShipmentAndNotInPackages());
        model.addAttribute("packages", packageService.getAllPackagesNotInShipments());
        return "shipment-new";
    }

    // CREATE SHIPMENT
    @PostMapping("/newShipment")
    public String createShipment(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> unitLoadIds,
            @RequestParam(required = false) List<Long> packageIds,
            @RequestParam(required = false) String fromLocation,
            @RequestParam(required = false) String toLocation,
            Model model
    ) {
        if (unitLoadIds == null) unitLoadIds = List.of();
        if (packageIds == null) packageIds = List.of();

        Set<Trackable> trackables = new HashSet<>();
        trackables.addAll(trackableService.getAllTrackablesById(unitLoadIds));
        trackables.addAll(trackableService.getAllTrackablesById(packageIds));

            try {
                shipmentService.create(null, description, fromLocation, toLocation, trackables);
                return "redirect:/web/shipments/list";
            } catch (InvalidDataException e) {
                model.addAttribute("errorMessage", "ERROR\nYou must specify al least one Trackable object");

                // IMPORTANTISSIMO: ricaricare anche i dati che servono alla pagina
                model.addAttribute("unitLoads",unitLoadService.getAllNotInShipmentAndNotInPackages());
                model.addAttribute("packages",packageService.getAllPackagesNotInShipments());

                return "shipment-new";
            }
    }

    //EDIT SHIPMENT

    @GetMapping("/shipments/{id}/edit")
    public String editShipmentPage(@PathVariable Long id, Model model) {
        Shipment shipment = shipmentService.getById(id);

        Set<Long> selectedUnitLoadIds = shipment.getTrackables().stream()
                .filter(t -> t instanceof UnitLoad)
                .map(Trackable::getId)
                .collect(Collectors.toSet());

        Set<Long> selectedPackageIds = shipment.getTrackables().stream()
                .filter(t -> t instanceof Package)
                .map(Trackable::getId)
                .collect(Collectors.toSet());

        prepareShipmentEditModel(shipment, selectedUnitLoadIds, selectedPackageIds, model);
        return "shipment-edit";
    }

    @PostMapping("/shipments/{id}/edit")
    public String editShipment(
            @PathVariable Long id,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> unitLoadIds,
            @RequestParam(required = false) List<Long> packageIds,
            @RequestParam(required = false) String fromLocation,
            @RequestParam(required = false) String toLocation,
            Model model
    ) {
        if (unitLoadIds == null) unitLoadIds = List.of();
        if (packageIds == null) packageIds = List.of();

        Set<Long> selectedUnitLoadIds = new HashSet<>(unitLoadIds);
        Set<Long> selectedPackageIds  = new HashSet<>(packageIds);

        Set<Long> allIds = new HashSet<>();
        allIds.addAll(unitLoadIds);
        allIds.addAll(packageIds);

        try {
            Shipment shipment = shipmentService.getById(id);

            Set<Trackable> trackables = new HashSet<>(trackableService.getAllTrackablesById(allIds));

            shipmentService.update(shipment, description, fromLocation, toLocation, null, null, trackables);
            return "redirect:/web/shipments/list";

        } catch (InvalidDataException e) {

            Shipment shipment = shipmentService.getById(id);

            model.addAttribute("errorMessage", "You must select at least one Unit Load or Package to update the shipment.");

            prepareShipmentEditModel(shipment, selectedUnitLoadIds, selectedPackageIds, model);

            // opzionale: per mostrare quello che l’utente aveva scritto anche se non salvato
            model.addAttribute("descriptionDraft", description);

            return "shipment-edit";
        }
    }

    @GetMapping("/shipments/{id}/complete")
    public String completeShipment(@PathVariable Long id, Model model) {
       Shipment shipment = shipmentService.getById(id);
        shipmentService.completeShipment(shipment);
       this.addBaseShipmentsModel(model);
       return "redirect:/web/shipments/list";
    }

    @GetMapping("/shipments/{id}/cancel")
    public String cancelShipment(@PathVariable Long id, Model model) {
        shipmentService.cancelShipment(id);
        this.addBaseShipmentsModel(model);
        return "redirect:/web/shipments/list";

    }



    // SHIPMENT DETAILS
    @GetMapping("/shipments/{id}")
    public String shipmentDetails(@PathVariable Long id, Model model) {
        model.addAttribute("shipment", shipmentService.getById(id));
        Set<Trackable> trackables = shipmentService.getById(id).getTrackables();
        Set<DeviceAssignment> deviceAssignaments =deviceAssignmentService.getActiveDeviceAssignmentsByTrackables(trackables);
        TrackingState t = TrackingState.INACTIVE;
        for(DeviceAssignment d : deviceAssignaments){
            t = d.getDevice().getTrackingState();
            break;
        }
        model.addAttribute("trackingState",t);

        return "shipment-details";
    }

    //UNITLOAD

    // NEW UNITLOAD (PAGE)
    @GetMapping("/unitloads/new")
    public String newUnitLoad(Model model) {
        model.addAttribute("loadItems", loadItemRepository.findAll());
        model.addAttribute("devices", deviceService.getAvailableDevices());
        return "unitload";
    }

    // CREATE UNITLOAD
    @PostMapping("/unitloads/new")
    public String createUnitLoad(
            @RequestParam String description,
            @RequestParam(required = false) Double temperatureValue,
            @RequestParam(required = false) TemperatureUnit temperatureUnit,
            @RequestParam(required = false) Integer humidityValue,
            @RequestParam(required = false) List<Long> loadItemIds,
            @RequestParam(required = false) List<Long> deviceIds,
            Model model
    ) {
        try {
            // 1) Build constraints
            Set<PhysicalConstraint> constraints = new HashSet<>();

            // TEMPERATURE
            if (temperatureValue != null) {
                if (temperatureUnit == null) {
                    throw new InvalidDataException("Temperature unit is required");
                }
                constraints.add(new TemperatureConstraint(temperatureValue, temperatureUnit));
            }

            // HUMIDITY
            if (humidityValue != null) {
                constraints.add(new HumidityConstraint(humidityValue.doubleValue()));
            }

            // 2) LoadItems (null-safe)
            Set<LoadItem> loadItems = new HashSet<>();
            if(loadItemIds == null || loadItemIds.isEmpty()){
                throw new InvalidDataException("loadItemIds is required");
            }else{
                loadItems = loadItemRepository.getLoadItemsByIdIn(loadItemIds);
            }


            // 3) Create UnitLoad
            UnitLoad u = unitLoadService.createUnitLoad(
                    description,
                    null,
                    constraints,
                    loadItems
            );

            // 4) Bind devices (null-safe)
            if(deviceIds == null || deviceIds.isEmpty()){
                throw new InvalidDataException("deviceIds is required");
            }

            for (Long deviceId : deviceIds) {
                deviceAssignmentService.bind(deviceId, u.getId());
            }

            return "redirect:/web/newShipment";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loadItems", loadItemRepository.findAll());
            model.addAttribute("devices", deviceService.getAvailableDevices());
            return "unitload"; // view, non redirect
        }
    }


    //PACKAGE

    // NEW PACKAGE (PAGE)
    @GetMapping("/packages/new")
    public String newPackage(Model model) {
        model.addAttribute("unitLoads", unitLoadService.getAllNotInShipmentAndNotInPackages());
        return "package";
    }

    // CREATE PACKAGE
    @PostMapping("/packages/new")
    public String createPackage(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> unitLoadIds,
            Model model
    ) {
        if (unitLoadIds == null){
            model.addAttribute("error","At least one UnitLoad must be selected to create a Package.");
            model.addAttribute("unitLoads", unitLoadService.getAllNotInShipmentAndNotInPackages());
            return "package";
        }
        Set<UnitLoad> unitloads = new HashSet<>();
        for(Long id : unitLoadIds) {
            unitloads.add(unitLoadService.getUnitLoadById(id));
        }
        packageService.createPackage(UUID.randomUUID().toString(),description, unitloads);
        return "redirect:/web/newShipment";
    }


    //TRACKABLES & DEVICES

    @GetMapping("/shipments/{id}/trackables")
    public String shipmentTrackablesAndDevices(@PathVariable Long id, Model model) {

        Shipment shipment = shipmentService.getById(id);
        Set<Trackable> trackables = shipment.getTrackables();

        // SOLO assegnamenti attivi (toTs is null)
        Map<Long, List<DeviceAssignment>> assignmentsByTrackableId =
                deviceAssignmentService.getActiveAssignmentsByTrackables(trackables);

        // SOLO device disponibili da poter bindare
        List<Device> availableDevices = deviceService.getAvailableDevices();

        model.addAttribute("shipment", shipment);
        model.addAttribute("trackables", trackables);
        model.addAttribute("assignmentsByTrackableId", assignmentsByTrackableId);
        model.addAttribute("availableDevices", availableDevices);

        return "shipment-trackable-devices";
    }

    //BIND

    @PostMapping("/trackables/{trackableId}/devices/bind")
    public String bindDeviceToTrackable(
            @PathVariable Long trackableId,
            @RequestParam Long shipmentId,
            @RequestParam Long deviceId
    ) {
        deviceAssignmentService.bind(deviceId, trackableId);
        //return "redirect:/web/shipments/list";
        return "redirect:/web/shipments/" + shipmentId + "/trackables";
    }

    //UNBIND

    @PostMapping("/trackables/{trackableId}/devices/unbind")
    public String unbindDeviceToTrackable(
            @PathVariable Long trackableId,
            @RequestParam Long deviceId,
            @RequestParam Long shipmentId
    ) {
        deviceAssignmentService.unbind(deviceId);
        //return "redirect:/web/shipments/list";
        return "redirect:/web/shipments/" + shipmentId + "/trackables";
    }

    //TRACKING

    // ACTIVATE tracking
    @PostMapping("/shipments/{id}/tracking/activate")
    public String activateTracking(@PathVariable Long id, Model model) {
        shipmentService.setTrackingState(id,TrackingState.ACTIVE);
        model.addAttribute("shipment", shipmentService.getById(id));
        model.addAttribute("trackingState",TrackingState.ACTIVE);
        return "redirect:/web/shipments/" + id;
    }

    // DEACTIVATE tracking
    @PostMapping("/shipments/{id}/tracking/deactivate")
    public String deactivateTracking(@PathVariable Long id, Model model) {
        shipmentService.setTrackingState(id, TrackingState.INACTIVE);
        model.addAttribute("shipment", shipmentService.getById(id));
        model.addAttribute("trackingState",TrackingState.INACTIVE);
        return "redirect:/web/shipments/" + id;
    }




    /*
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
*/

    // HELPERS

    private void prepareShipmentEditModel(Shipment shipment,
                                          Set<Long> selectedUnitLoadIds,
                                          Set<Long> selectedPackageIds,
                                          Model model) {

        model.addAttribute("shipment", shipment);
        model.addAttribute("selectedUnitLoadIds", selectedUnitLoadIds);
        model.addAttribute("selectedPackageIds", selectedPackageIds);

        Set<UnitLoad> selectedUnitLoads = new HashSet<>();
        Set<Package> selectedPackages = new HashSet<>();

        for  (Long id : selectedUnitLoadIds) {
            selectedUnitLoads.add(unitLoadService.getUnitLoadById(id));
        }
        for  (Long id : selectedPackageIds) {
            selectedPackages.add(packageService.getPackageById(id));
        }


        // disponibili
        var availableUnitLoads = unitLoadService.getAllNotInShipmentAndNotInPackages();
        var availablePackages  = packageService.getAllPackagesNotInShipments();

        // merge senza duplicati (preserva ordine)
        Map<Long, UnitLoad> unitLoadsToShow = new LinkedHashMap<>();
        for (UnitLoad ul : selectedUnitLoads) unitLoadsToShow.put(ul.getId(), ul);
        for (UnitLoad ul : availableUnitLoads) unitLoadsToShow.put(ul.getId(), ul);

        Map<Long, Package> packagesToShow = new LinkedHashMap<>();
        for (Package p : selectedPackages) packagesToShow.put(p.getId(), p);
        for (Package p : availablePackages) packagesToShow.put(p.getId(), p);

        model.addAttribute("unitLoadsToShow", unitLoadsToShow.values());
        model.addAttribute("packagesToShow", packagesToShow.values());
    }


}
