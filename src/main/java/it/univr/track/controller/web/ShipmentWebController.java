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
            Model model
    ) {
        if (unitLoadIds == null) unitLoadIds = List.of();
        if (packageIds == null) packageIds = List.of();

        Set<Trackable> trackables = new HashSet<>();
        trackables.addAll(trackableService.getAllTrackablesById(unitLoadIds));
        trackables.addAll(trackableService.getAllTrackablesById(packageIds));

            try {
                shipmentService.create(null, description, trackables);
                return "redirect:/web/shipments/list";
            } catch (InvalidDataException e) {
                model.addAttribute("errorMessage", e.getMessage());

                // IMPORTANTISSIMO: ricaricare anche i dati che servono alla pagina (liste unitload/package ecc)
                //model.addAttribute("availableTrackables", trackableService.getAllTrackablesNotInShipments());
                // + qualsiasi altra roba che la view usa (altrimenti NPE in thymeleaf)
                // da controllare
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
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> selectedPackageIds = shipment.getTrackables().stream()
                .filter(t -> t instanceof Package)
                .map(Trackable::getId)
                .collect(java.util.stream.Collectors.toSet());

        prepareShipmentEditModel(shipment, selectedUnitLoadIds, selectedPackageIds, model);
        return "shipment-edit";
    }

    @PostMapping("/shipments/{id}/edit")
    public String editShipment(
            @PathVariable Long id,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> unitLoadIds,
            @RequestParam(required = false) List<Long> packageIds,
            Model model
    ) {
        if (unitLoadIds == null) unitLoadIds = List.of();
        if (packageIds == null) packageIds = List.of();

        Set<Long> selectedUnitLoadIds = new java.util.HashSet<>(unitLoadIds);
        Set<Long> selectedPackageIds  = new java.util.HashSet<>(packageIds);

        Set<Long> allIds = new java.util.HashSet<>();
        allIds.addAll(unitLoadIds);
        allIds.addAll(packageIds);

        try {
            Shipment shipment = shipmentService.getById(id);

            Set<Trackable> trackables = new HashSet<>(trackableService.getAllTrackablesById(allIds));

            shipmentService.update(shipment, description, null, null, trackables);
            return "redirect:/web/shipments/list";

        } catch (InvalidDataException e) {

            Shipment shipment = shipmentService.getById(id);

            model.addAttribute("errorMessage", e.getMessage());

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
            @RequestParam(required = false) String constraintsRaw,
            @RequestParam(required = false) List<Long> loadItemIds,
            @RequestParam(required = false) List<Long> deviceIds,
            Model model
    ) {
        // parse constraints raw
        List<PhysicalConstraint> c = parseConstraintsRaw(constraintsRaw);
        Set<PhysicalConstraint> constraints = new HashSet<>(c);
        try{
            Set<LoadItem> loadItems = loadItemRepository.getLoadItemsByIdIn(loadItemIds);

            UnitLoad u = unitLoadService.createUnitLoad(description,null, constraints, loadItems);

            for(Long id : deviceIds) {
                deviceAssignmentService.bind(id,u.getId());
            }
        } catch (Exception e) {
            model.addAttribute("loadItems", loadItemRepository.findAll());
            model.addAttribute("devices", deviceService.getAvailableDevices());
            return "redirect:/web/unitloads/new";
        }

        return "redirect:/web/newShipment";
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
            @RequestParam List<Long> unitLoadIds,
            Model model
    ) {
        if (unitLoadIds == null) unitLoadIds = List.of();
        Set<UnitLoad> unitloads = new HashSet<>();
        for(Long id : unitLoadIds) {
            unitloads.add(unitLoadService.getUnitLoadById(id));
        }
        packageService.createPackage(UUID.randomUUID().toString(),description, unitloads);
        model.addAttribute("unitLoads", unitLoadService.getAllUnitloadsNotInShipment());
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
            @RequestParam Long deviceId
    ) {
        deviceAssignmentService.bind(deviceId, trackableId);
        return "redirect:/web/shipments/list";
        // oppure meglio: return "redirect:/web/shipments/" + shipmentId + "/trackables";
    }

    //UNBIND

    @PostMapping("/trackables/{trackableId}/devices/unbind")
    public String unbindDeviceToTrackable(
            @RequestParam Long deviceId
    ) {
        deviceAssignmentService.unbind(deviceId);
        return "redirect:/web/shipments/list";
        // oppure meglio: return "redirect:/web/shipments/" + shipmentId + "/trackables";
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

    private static List<PhysicalConstraint> parseConstraintsRaw(String constraintsRaw) {
        List<PhysicalConstraint> out = new ArrayList<>();
        if (constraintsRaw == null || constraintsRaw.isBlank()) return out;

        String[] lines = constraintsRaw.split("\\R"); // split su newline
        for (String line : lines) {
            String s = line.trim();
            if (s.isEmpty()) continue;

            String[] parts = s.split("\\s*;\\s*");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid constraint line: " + line);
            }

            PhysicalConstraintType type = PhysicalConstraintType.valueOf(parts[0].trim().toUpperCase());

            switch (type) {
                case HUMIDITY -> {
                    double percent = Double.parseDouble(parts[1]);
                    out.add(new HumidityConstraint(percent));
                }
                case TEMPERATURE -> {
                    double value = Double.parseDouble(parts[1]);
                    TemperatureUnit unit = (parts.length >= 3 && !parts[2].isBlank())
                            ? TemperatureUnit.valueOf(parts[2].trim().toUpperCase())
                            : TemperatureUnit.CELSIUS;
                    out.add(new TemperatureConstraint(value, unit));
                }
            }
        }
        return out;
    }

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
        Map<Long, UnitLoad> unitLoadsToShow = new java.util.LinkedHashMap<>();
        for (UnitLoad ul : selectedUnitLoads) unitLoadsToShow.put(ul.getId(), ul);
        for (UnitLoad ul : availableUnitLoads) unitLoadsToShow.put(ul.getId(), ul);

        Map<Long, Package> packagesToShow = new java.util.LinkedHashMap<>();
        for (Package p : selectedPackages) packagesToShow.put(p.getId(), p);
        for (Package p : availablePackages) packagesToShow.put(p.getId(), p);

        model.addAttribute("unitLoadsToShow", unitLoadsToShow.values());
        model.addAttribute("packagesToShow", packagesToShow.values());
    }


}
