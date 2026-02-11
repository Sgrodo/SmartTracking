package it.univr.track.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import it.univr.track.bdd.TestWorld;
import it.univr.track.entity.Device;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.*;
import it.univr.track.service.*;
import it.univr.track.web.po.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

public class BddSteps {

        private final TestWorld world;

        // DATA setup
        private final LoadItemRepository loadItemRepository;
        private final UnitLoadService unitLoadService;
        private final PackageService packageService;
        private final ShipmentService shipmentService;
        private final DeviceAssignmentService deviceAssignmentService;

        // Repos utili per assert “exist”
        private final ShipmentRepository shipmentRepository;
        private final UnitLoadRepository unitLoadRepository;
        private final PackageRepository packageRepository;
        private final DeviceRepository deviceRepository;

        public BddSteps(
                TestWorld world,
                LoadItemRepository loadItemRepository,
                UnitLoadService unitLoadService,
                PackageService packageService,
                ShipmentService shipmentService,
                ShipmentRepository shipmentRepository,
                UnitLoadRepository unitLoadRepository,
                PackageRepository packageRepository,
                DeviceAssignmentService deviceAssignmentService,
                DeviceRepository deviceRepository
        ) {
            this.world = world;
            this.loadItemRepository = loadItemRepository;
            this.unitLoadService = unitLoadService;
            this.packageService = packageService;
            this.shipmentService = shipmentService;
            this.shipmentRepository = shipmentRepository;
            this.unitLoadRepository = unitLoadRepository;
            this.packageRepository = packageRepository;
            this.deviceAssignmentService = deviceAssignmentService;
            this.deviceRepository = deviceRepository;

            // “implicit wait” base
            world.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        }

    private String resolveUrl(String pageName) {
        return switch (pageName) {
            case "Shipments list" -> "/shipments/list";
            case "Create shipment" -> "/newShipment";
            case "Create UnitLoad" -> "/unitloads/new";
            case "Create Package" -> "/packages/new";
            default -> throw new IllegalArgumentException("Unknown page: " + pageName);
        };
    }

    @Before
    public void resetWorld() {
        world.shipmentIdByDescription.clear();
        world.trackableIdByDescription.clear();
        world.deviceIdByOrigin.clear();
    }


    /// *
    /// SHIPMENTS.feature file
    ///
    //# Scenario 1

    @Given("I am logged in as {string}")
    public void iAmLoggedInAs(String username) {
        // assunzione vera
    }

    @Transactional
    @Given("there is an available UnitLoad with description {string} and loadItem {string}")
    public void thereIsAnAvailableUnitLoadWithDescriptionAndLoadItem(String unitLoadDesc, String loadItemName) {
        LoadItem item = new LoadItem(loadItemName, java.util.UUID.randomUUID().toString(), loadItemName);
        item = loadItemRepository.save(item);

        // crea UL disponibile (non in shipment/package)
        UnitLoad ul = unitLoadService.createUnitLoad(unitLoadDesc, null, null, java.util.List.of(item));

        // cache opzionale
        world.trackableIdByDescription.put(unitLoadDesc, ul.getId());
    }

    @Transactional
    @And("there is an available Package with description {string}")
    public void thereIsAnAvailablePackageWithDescription(String description) {
        LoadItem l = loadItemRepository.save(
                new LoadItem("ciliegie", UUID.randomUUID().toString(), "ciliegie")
        );

        UnitLoad u = unitLoadService.createUnitLoad(description, null, null, List.of(l));


        Package p = packageService.createPackage(
                UUID.randomUUID().toString(),
                description,
                Set.of(u)
        );

        Device device = deviceRepository.save(new Device(true,true,true, new BigDecimal("15.0"), "France"));
        deviceAssignmentService.bind(device.getId(),p.getId());

        Assertions.assertNotNull(p.getId());
        Assertions.assertTrue(packageRepository.findById(p.getId()).isPresent());
    }

    @When("I open the {string} page")
    public void iOpenThePage(String pageName) {
        world.driver.get(world.baseUrl + resolveUrl(pageName));

        switch (pageName) {
            case "Shipments list" -> world.shipmentListPage = new ShipmentListPage(world.driver);
            case "Create shipment" -> world.createShipmentPage = new CreateShipmentPage(world.driver);
            case "Create UnitLoad" -> world.createUnitLoadPage = new CreateUnitLoadPage(world.driver);
            case "Create Package" -> world.createPackagePage = new CreatePackagePage(world.driver);
            default -> throw new IllegalArgumentException("Unsupported page: " + pageName);
        }
    }

    @And("I enter from-location {string} and to-location {string}")
    public void iEnterFromLocationAndToLocation(String from, String to) {
        world.createShipmentPage.setFromLocation(from);
        world.createShipmentPage.setToLocation(to);

        assertEquals(from, world.createShipmentPage.getFromLocation());
        assertEquals(to, world.createShipmentPage.getToLocation());
    }

    @And("I enter shipment description {string}")
    public void iEnterShipmentDescription(String description) {
        world.createShipmentPage.insertDescription(description);
    }

    @And("I select UnitLoad {string}")
    public void iSelectUnitLoad(String description) {
        List<UnitLoad>unitloads = unitLoadRepository.findByDescription(description);

        assertEquals(1, unitloads.size());
        boolean flag = false;
        Long id = null;
        for(UnitLoad u : unitloads) {
            if (u.getDescription().equals(description)) {
                id = u.getId();
                flag = true;
                break;
            }
        }
        assertTrue(flag);
        assertNotNull(id);
        world.createShipmentPage.selectUniLoad(id);
    }

    @And("I select Package {string}")
    public void iSelectPackage(String description) {
        Long pckId = packageRepository.findByDescription(description).orElseThrow(
                () -> new NotFoundException("Package not found: " + description)
        ).getId();
        world.createShipmentPage.selectPackage(pckId);
    }

    @And("I submit the shipment creation")
    public void iSubmitTheShipmentCreation() {
        world.createShipmentPage.createShipment();
    }

    @Then("I should be redirected to the {string} page")
    public void iShouldBeRedirectedToThePage(String pageName) {
        String expectedUrl = world.baseUrl + resolveUrl(pageName);
        String currentUrl = world.driver.getCurrentUrl();

        Assertions.assertTrue(
                currentUrl.endsWith(resolveUrl(pageName)),
                "Expected redirect to " + expectedUrl + " but was " + currentUrl
        );
    }

    @Transactional
    @And("the shipment {string} should exist with tracking state {string}")
    public void theShipmentShouldExistWithTrackingState(String description, String trackingState) {
        Shipment shipment = shipmentRepository.findByDescription(description)
                .orElseThrow(() -> new NotFoundException("Shipment not found: " + description));

        // Se trackingState è enum
        TrackingState expectedState = TrackingState.valueOf(trackingState);

        assertEquals(
                expectedState,
                shipmentService.getTrackingState(shipment),
                "Tracking state mismatch"
        );
    }

    //# Scenario 1 (validation case)

    @Then("I should remain on the {string} page")
    public void iShouldRemainOnThePage(String pageName) {
        if (pageName.equals("Create UnitLoad")) {
            world.createUnitLoadPage = new CreateUnitLoadPage(world.driver);
            org.junit.jupiter.api.Assertions.assertTrue(
                    world.createUnitLoadPage.getTitle().toLowerCase().contains("unitload"),
                    "Not on Create UnitLoad page (title=" + world.createUnitLoadPage.getTitle() + ")"
            );
            return;
        }

        if (pageName.equals("Create Package")) {
            world.createPackagePage = new CreatePackagePage(world.driver);
            org.junit.jupiter.api.Assertions.assertTrue(
                    world.createPackagePage.getTitle().toLowerCase().contains("package"),
                    "Not on Create Package page (title=" + world.createPackagePage.getTitle() + ")"
            );
            return;
        }

        throw new IllegalArgumentException("Unsupported page: " + pageName);
    }

    @And("I should see an error message")
    public void iShouldSeeAnErrorMessage() {
        world.createUnitLoadPage = new CreateUnitLoadPage(world.driver);
        org.junit.jupiter.api.Assertions.assertTrue(world.createUnitLoadPage.hasError(), "Expected an error message, but none was shown.");
    }

    //# Scenario 4

    @Transactional
    @Given("there is an existing shipment with description {string}")
    public void thereIsAnExistingShipmentWithDescription(String description) {

        // 1. Creo un LoadItem
        LoadItem l = loadItemRepository.save(
                new LoadItem("auto-item", UUID.randomUUID().toString(), "auto-item")
        );

        // 2. Creo un UnitLoad
        UnitLoad ul = unitLoadService.createUnitLoad(
                "UL-for-" + description,
                null,
                null,
                List.of(l)
        );

        // 3. Creo lo Shipment con il trackable
        Shipment shipment = shipmentService.create(
                null,
                description,
                "null",
                "null",
                Set.of(ul)
        );

        Assertions.assertNotNull(shipment.getId());
    }

    @Then("I should see shipment {string} in the list")
    public void iShouldSeeShipmentInTheList(String description) {
        Iterable<ShipmentListPage.ShipmentRow> rows = world.shipmentListPage.getActiveShipments();

        boolean found = StreamSupport.stream(rows.spliterator(), false)
                .anyMatch(r -> r.description().equals(description));

        Assertions.assertTrue(found, "Shipment '" + description + "' not found in active shipments table");
    }

    @Transactional
    @When("I open details for shipment {string}")
    public void iOpenDetailsForShipment(String description) {
        world.driver.get("http://localhost:8080/web/shipments/list");
        world.shipmentListPage = new ShipmentListPage(world.driver);
        Iterable<ShipmentListPage.ShipmentRow> rows =
                world.shipmentListPage.getActiveShipments();

        ShipmentListPage.ShipmentRow row = StreamSupport
                .stream(rows.spliterator(), false)
                .filter(r -> r.description().equals(description))
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Shipment not found in list: " + description)
                );

        world.shipmentDetailsPage = row.clickDetails();
    }

    @Then("I should see the shipment details page for {string}")
    public void iShouldSeeTheShipmentDetailsPageFor(String description) {
        String pageSource = world.driver.getPageSource();

        Assertions.assertTrue(
                pageSource.contains(description),
                "Shipment details page does not contain description: " + description
        );
    }

    //# Scenario 5
    @Transactional
    @Given("there is an existing shipment with description {string} and tracking state {string}")
    public void thereIsAnExistingShipmentWithDescriptionAndTrackingState(String description, String state) {

        // 1. Creo un LoadItem
        LoadItem l = loadItemRepository.save(
                new LoadItem("auto-item", UUID.randomUUID().toString(), "auto-item")
        );

        // 2. Creo un UnitLoad
        UnitLoad ul = unitLoadService.createUnitLoad(
                "UL-for-" + description,
                null,
                null,
                List.of(l)
        );

        Device device = deviceRepository.save(new Device(true,true,true, new BigDecimal("10.0"), "Italy"));
        deviceAssignmentService.bind(device.getId(),ul.getId());

        // 3. Creo lo Shipment
        Shipment shipment = shipmentService.create(
                null,
                description,
                "null",
                "null",
                Set.of(ul)
        );

        // 4. Imposto lo stato richiesto
        TrackingState desiredState = TrackingState.valueOf(state);

        shipmentService.setTrackingState(shipment.getId(), desiredState);

        // 5. Verifica
        assertEquals(
                desiredState,
                shipmentService.getTrackingState(shipment),
                "Tracking state not set correctly"
        );

        world.lastShipment = shipment;
    }

    @And("I click {string}")
    public void iClick(String buttonText) {

        switch (buttonText.toLowerCase()) {
            case "activate tracking" -> world.shipmentDetailsPage = world.shipmentDetailsPage.activateTracking();
            case "deactivate tracking" -> world.shipmentDetailsPage = world.shipmentDetailsPage.deActivateTracking();
            default -> Assertions.fail("Invalid button text");
        }

    }

    @Then("shipment {string} tracking state should be {string}")
    public void shipmentTrackingStateShouldBe(String description, String trackingState) {

        TrackingState desiredState = TrackingState.valueOf(trackingState);
       TrackingState actualState =  TrackingState.valueOf(world.shipmentDetailsPage.getTrackingState());

       assertEquals(desiredState, actualState, "Tracking state not set correctly");
    }

    /// *
    ///UNITLOAD_AND_PACKAGE.feature
    ///
    //# Scenario 2
    //# Scenario 2 (validation case)

    @Transactional
    @Given("there is a warehouse LoadItem with name {string}")
    public void thereIsAWarehouseLoadItemWithName(String name) {
        // crea un LoadItem "da magazzino"
        LoadItem item = new LoadItem(name, java.util.UUID.randomUUID().toString(), name);
        loadItemRepository.save(item);
    }

    @Transactional
    @Given("there is a device with origin {string}")
    public void thereIsADeviceWithOrigin(String origin) {
        Device d = new Device(true, true, true, new java.math.BigDecimal("10.0"), origin);
        d = deviceRepository.save(d);


        world.deviceIdByOrigin.put(origin, d.getId());
    }


    @And("I enter unitload description {string}")
    public void iEnterUnitloadDescription(String description) {
        world.createUnitLoadPage.setDescription(description);
    }

    @And("I set humidity constraint to {double}")
    public void iSetHumidityConstraintTo(double value) {
        world.createUnitLoadPage.setHumidityConstraint(value);
    }

    @Transactional
    @And("I select LoadItem {string}")
    public void iSelectLoadItem(String loadItemName) {
        boolean ok = world.createUnitLoadPage.selectLoadItemByDescription(loadItemName);
        org.junit.jupiter.api.Assertions.assertTrue(ok, "LoadItem not found in table: " + loadItemName);
    }


    @And("I select Device with origin {string}")
    public void iSelectDeviceWithOrigin(String origin) {

        Assertions.assertTrue(
                world.createUnitLoadPage.containsDeviceOrigin(origin),
                "Device origin not present in devices table: " + origin
        );

        boolean selected = world.createUnitLoadPage.selectDeviceByOrigin(origin);

        Assertions.assertTrue(selected, "Could not select device checkbox for origin: " + origin);
    }


    @And("I submit the unitload creation")
    public void iSubmitTheUnitloadCreation() {
        world.createUnitLoadPage.submit();
    }


    @Transactional
    @Then("a UnitLoad {string} should exist")
    public void aUnitLoadShouldExist(String description) {
        // postcondizione: ok controllare DB
        boolean exists = unitLoadRepository.findAll().stream()
                .anyMatch(ul -> description.equals(ul.getDescription()));
        org.junit.jupiter.api.Assertions.assertTrue(exists, "UnitLoad not found in DB with description: " + description);
    }

    //# Scenario 3
    //# Scenario 3 (validation case)

    @And("I enter package description {string}")
    public void iEnterPackageDescription(String description) {
        world.createPackagePage.setDescription(description);
    }

    @And("I select UnitLoad {string} for package")
    public void iSelectUnitLoadForPackage(String unitLoadDescription) {
        boolean ok = world.createPackagePage.selectUnitLoadByDescription(unitLoadDescription);
        org.junit.jupiter.api.Assertions.assertTrue(ok, "UnitLoad not found in table: " + unitLoadDescription);
    }

    @And("I submit the package creation")
    public void iSubmitThePackageCreation() {
        world.createPackagePage.submit();
    }

    @Transactional
    @Then("a Package {string} should exist containing UnitLoads {string} and {string}")
    public void aPackageShouldExistContainingUnitLoads(String packageDesc, String ul1Desc, String ul2Desc) {
        Package pkg = packageRepository.findAll().stream()
                .filter(p -> packageDesc.equals(p.getDescription()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Package not found in DB with description: " + packageDesc));

        java.util.Set<String> unitLoadDescriptions = pkg.getUnitLoads().stream()
                .map(UnitLoad::getDescription)
                .collect(java.util.stream.Collectors.toSet());

        org.junit.jupiter.api.Assertions.assertTrue(unitLoadDescriptions.contains(ul1Desc),
                "Package does not contain UnitLoad: " + ul1Desc + " (contains=" + unitLoadDescriptions + ")");
        org.junit.jupiter.api.Assertions.assertTrue(unitLoadDescriptions.contains(ul2Desc),
                "Package does not contain UnitLoad: " + ul2Desc + " (contains=" + unitLoadDescriptions + ")");
    }


    @And("I should see an error message on Create Package page")
    public void iShouldSeeAnErrorMessageOnCreatePackagePage() {
        world.createPackagePage = new CreatePackagePage(world.driver);
        org.junit.jupiter.api.Assertions.assertTrue(world.createPackagePage.hasError(), "Expected an error message on Create Package page, but none was shown.");
    }


    /// *
    ///DEVICE_BINDING.feature
    ///
    //# Scenario 6

    @Transactional
    @Given("there is a non-terminated shipment with description {string}")
    public void thereIsANonTerminatedShipmentWithDescription(String description) {

        LoadItem item = new LoadItem("banana", UUID.randomUUID().toString(), "banana");
        item = loadItemRepository.save(item);

        UnitLoad ul1 = unitLoadService.createUnitLoad("T1", null, null, List.of(item));
        UnitLoad ul2 = unitLoadService.createUnitLoad("T2", null, null, List.of(item));

        world.trackableIdByDescription.put("T1", ul1.getId());
        world.trackableIdByDescription.put("T2", ul2.getId());

        Device d1 = deviceRepository.save(new Device(true, true, true, new BigDecimal("10.0"), "D1"));
        world.deviceIdByOrigin.put("D1", d1.getId());   // <-- QUESTO

        Shipment created = shipmentService.create(null, description, "Mialn", "Venice", Set.of(ul1, ul2));
        world.shipmentIdByDescription.put(description, created.getId());
    }

    @Transactional
    @And("shipment {string} has trackable {string}")
    public void shipmentHasTrackable(String shipmentDescription, String trackableDescription) {
        // Given: ok interrogare DB
        Shipment shipment = shipmentRepository.findByDescription(shipmentDescription)
                .orElseThrow(() -> new AssertionError("Shipment not found: " + shipmentDescription));

        Long trackableId = world.requireTrackableId(trackableDescription);

        boolean present = shipment.getTrackables().stream().anyMatch(t -> {
            if (t instanceof UnitLoad ul) return ul.getId().equals(trackableId);
            if (t instanceof it.univr.track.entity.data.Package p) return p.getId().equals(trackableId);
            return false;
        });

        Assertions.assertTrue(
                present,
                "Shipment " + shipmentDescription + " does not contain trackable " + trackableDescription + " (id=" + trackableId + ")"
        );
    }


    @Transactional
    @And("there is an available device with code {string}")
    public void thereIsAnAvailableDeviceWithCode(String origin) {
        // Given: setup device (se già esiste, riusa)
        Device d = deviceRepository.findByOrigin(origin)
                .orElseGet(() -> deviceRepository.save(new Device(true, true, true, new BigDecimal("10.0"), origin)));

        // cache id per uso futuro nei Given (e per debug)
        world.deviceIdByOrigin.put(origin, d.getId());
    }

    @When("I open the {string} page for shipment {string}")
    public void iOpenThePageForShipment(String pageName, String shipmentDescription) {

        if (!pageName.equals("Trackables & Devices")) {
            throw new IllegalArgumentException("Unsupported page: " + pageName);
        }

        world.driver.get(world.baseUrl + "/shipments/list");
        world.shipmentListPage = new ShipmentListPage(world.driver);

        world.trackablesDevicesPage = world.shipmentListPage
                .getActiveShipmentByDescription(shipmentDescription)
                .clickTrackablesAndDevices();
    }



    @And("I bind device {string} to trackable {string}")
    public void iBindDeviceToTrackable(String origin, String trackableDescription) {
        Long trackableId = world.requireTrackableId(trackableDescription);

        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        Assertions.assertNotNull(section, "Trackable section not found for id: " + trackableId);

        // SOLO selenium: select option by origin + submit
        world.trackablesDevicesPage = section.bindDeviceByOrigin(origin);
    }

    @Then("trackable {string} should show device {string} as bound")
    public void trackableShouldShowDeviceAsBound(String trackableDescription, String origin) {
        Long trackableId = world.requireTrackableId(trackableDescription);

        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        Assertions.assertNotNull(section, "Trackable section not found for id: " + trackableId);

        Assertions.assertTrue(
                section.hasBoundDeviceWithOrigin(origin),
                "Expected device " + origin + " to be bound in trackable " + trackableDescription + " (id=" + trackableId + ")"
        );
    }


    @And("device {string} should not be in the list of available devices")
    public void deviceShouldNotBeInTheListOfAvailableDevices(String origin) {
        // SOLO selenium: non deve comparire in nessuna select "Choose device..."
        boolean found = world.trackablesDevicesPage.isDeviceOriginAvailableSomewhere(origin);

        Assertions.assertFalse(
                found,
                "Device " + origin + " is still shown as available in at least one trackable section"
        );
    }

    //# Scenario 7

    @Transactional
    @And("device {string} is bound to trackable {string} in shipment {string}")
    public void deviceIsBoundToTrackableInShipment(String origin, String trackableDescription, String shipmentDescription) {
        // Given -> ok usare service
        Long shipmentId = world.requireShipmentId(shipmentDescription);
        Long trackableId = world.requireTrackableId(trackableDescription);
        Long deviceId = world.requireDeviceId(origin);

        // (optional) sanity check shipment exists
        Assertions.assertTrue(shipmentRepository.findById(shipmentId).isPresent(),
                "Shipment not found by cached id: " + shipmentId);

        // bind via service
        deviceAssignmentService.bind(deviceId, trackableId);
    }

    @And("I unbind device {string} from trackable {string}")
    public void iUnbindDeviceFromTrackable(String origin, String trackableDescription) {
        Long trackableId = world.requireTrackableId(trackableDescription);

        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        Assertions.assertNotNull(section, "Trackable section not found on page for id: " + trackableId);

        // SOLO selenium: click sul bottone Unbind della riga con origin
        world.trackablesDevicesPage = section.unbindDeviceByOrigin(origin);
    }

    @Then("trackable {string} should not show any bound device")
    public void trackableShouldNotShowAnyBoundDevice(String trackableDescription) {
        Long trackableId = world.requireTrackableId(trackableDescription);

        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        Assertions.assertNotNull(section, "Trackable section not found on page for id: " + trackableId);

        Assertions.assertFalse(
                section.hasAnyBoundDevice(),
                "Expected no bound devices for trackable " + trackableDescription + " (id=" + trackableId + ")"
        );
    }

    @And("device {string} should be in the list of available devices")
    public void deviceShouldBeInTheListOfAvailableDevices(String origin) {
        // SOLO selenium: il device deve comparire in ALMENO una select "Choose device"
        boolean found = world.trackablesDevicesPage.isDeviceOriginAvailableSomewhere(origin);

        Assertions.assertTrue(
                found,
                "Device " + origin + " is not shown as available in any trackable section"
        );
    }


}
