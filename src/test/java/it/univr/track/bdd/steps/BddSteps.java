package it.univr.track.bdd.steps;

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
    public void thereIsAnAvailableUnitLoadWithDescriptionAndLoadItem(String description, String loaditem) {
        LoadItem l = loadItemRepository.save(
                new LoadItem(loaditem, UUID.randomUUID().toString(), loaditem)
        );

        UnitLoad ul = unitLoadService.createUnitLoad(description, null, null, List.of(l));
        Device device = deviceRepository.save(new Device(true,true,true, new BigDecimal("10.0"), "Italy"));
        deviceAssignmentService.bind(device.getId(),ul.getId());
        // Assert leggero di sicurezza
        Assertions.assertNotNull(ul.getId());
        Assertions.assertTrue(unitLoadRepository.findById(ul.getId()).isPresent());
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
            case "Shipments list" ->
                    world.shipmentListPage = new ShipmentListPage(world.driver);
            case "Create shipment" ->
                    world.createShipmentPage = new CreateShipmentPage(world.driver);
            case "Create UnitLoad" ->
                    world.createUnitLoadPage = new CreateUnitLoadPage(world.driver);
            case "Create Package" ->
                    world.createPackagePage = new CreatePackagePage(world.driver);
            default -> throw new IllegalArgumentException("Unknown page: " + pageName);
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
        String expectedPath = resolveUrl(pageName);
        String currentUrl = world.driver.getCurrentUrl();

        Assertions.assertTrue(
                currentUrl.contains(expectedPath),
                "Expected to remain on " + expectedPath + " but was " + currentUrl
        );
    }

    @And("I should see an error message")
    public void iShouldSeeAnErrorMessage() {
        Assertions.assertTrue(world.createShipmentPage.hasError(), "Error message is not displayed");
        Assertions.assertFalse(world.createShipmentPage.getError().isBlank(), "Error message is empty");
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
        LoadItem loadItem = new LoadItem(
                name,
                UUID.randomUUID().toString(),
                name
        );

        loadItemRepository.save(loadItem);

        Assertions.assertTrue(
                loadItemRepository.findByName(name).isPresent(),
                "LoadItem not created in DB"
        );
    }

    @Transactional
    @Given("there is a device with origin {string}")
    public void thereIsADeviceWithOrigin(String origin) {
        Device device = new Device(
                false,
                false,
                true,
                new BigDecimal("10.0"),
                origin
        );

        deviceRepository.save(device);
    }


    @And("I enter unitload description {string}")
    public void iEnterUnitloadDescription(String description) {
        world.createUnitLoadPage.setDescription(description);

        Assertions.assertEquals(
                description,
                world.createUnitLoadPage.getDescription(),
                "UnitLoad description not set correctly"
        );
    }

    @And("I set humidity constraint to {double}")
    public void iSetHumidityConstraintTo(Double value) {
        world.createUnitLoadPage.setHumidityConstraint(value);
        assertEquals(String.valueOf(value),world.createUnitLoadPage.getHumidityConstraintValue());
    }

    @Transactional
    @And("I select LoadItem {string}")
    public void iSelectLoadItem(String name) {

        LoadItem loadItem = loadItemRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("LoadItem not found: " + name));

        // prima verifica che la riga esista nella tabella (stesso xpath robusto)
        Assertions.assertTrue(
                world.createUnitLoadPage.containsLoadItemId(loadItem.getId()),
                "LoadItem id not present in table (maybe page not refreshed or not loaded yet): " + loadItem.getId()
        );

        boolean selected = world.createUnitLoadPage.selectLoadItem(loadItem.getId());
        Assertions.assertTrue(selected, "Could not select LoadItem checkbox");
    }


    @And("I select Device with origin {string}")
    public void iSelectDeviceWithOrigin(String origin) {
        Device device = deviceRepository.findByOrigin(origin)
                .orElseThrow(() -> new NotFoundException("Device not found with origin: " + origin));

        boolean selected = world.createUnitLoadPage.selectDevice(device.getId());

        Assertions.assertTrue(selected, "Could not select device checkbox");

        Assertions.assertTrue(
                world.createUnitLoadPage.containsDeviceId(device.getId()),
                "Device id not present in devices table"
        );
    }


    @And("I submit the unitload creation")
    public void iSubmitTheUnitloadCreation() {
        world.createShipmentPage = world.createUnitLoadPage.clickCreateUnitLoad();

    }

    @Transactional
    @Then("a UnitLoad {string} should exist")
    public void aUnitLoadShouldExist(String description) {
        //List<UnitLoad>unitloads = unitLoadRepository.findByDescription(description);
        List<UnitLoad>unitloads = unitLoadRepository.findAll();
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
    }

    //# Scenario 3
    //# Scenario 3 (validation case)

    @And("I enter package description {string}")
    public void iEnterPackageDescription(String description) {
        world.createPackagePage.setDescription(description);
        Assertions.assertEquals(description, world.createPackagePage.getDescription());
    }

    @And("I select UnitLoad {string} for package")
    public void iSelectUnitLoadForPackage(String description) {
        UnitLoad ul = unitLoadRepository.findFirstByDescription(description)
                .orElseThrow(() -> new RuntimeException("UnitLoad not found: " + description));

        world.createPackagePage.selectUnitLoad(String.valueOf(ul.getId()));

    }

    @And("I submit the package creation")
    public void iSubmitThePackageCreation() {
        world.shipmentListPage = world.createPackagePage.createPackage();
    }

    @Transactional
    @Then("a Package {string} should exist containing UnitLoads {string} and {string}")
    public void aPackageShouldExistContainingUnitLoadsAnd(String packageDescription, String ulDesc1, String ulDesc2) {

            Package pkg = packageRepository.findByDescription(packageDescription)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Package not found"));

            Set<String> containedDescriptions = pkg.getUnitLoads()
                    .stream()
                    .map(UnitLoad::getDescription)
                    .collect(Collectors.toSet());

            assertThat(containedDescriptions)
                    .contains(ulDesc1, ulDesc2);
    }


    @And("I should see an error message on Create Package page")
    public void iShouldSeeAnErrorMessageOnCreatePackagePage() {
        Assertions.assertTrue(world.createPackagePage.hasError(), "Error message is not displayed");
        Assertions.assertFalse(world.createPackagePage.getError().isBlank(), "Error message is empty");
    }


    /// *
    ///DEVICE_BINDING.feature
    ///
    //# Scenario 6

    @Transactional
    @Given("there is a non-terminated shipment with description {string}")
    public void thereIsANonTerminatedShipmentWithDescription(String description) {
        // 1) Create LoadItem (oggetto presente in magazzino)
        LoadItem item = new LoadItem("banana", UUID.randomUUID().toString(), "banana");// o name/sku, come hai tu
        item = loadItemRepository.save(item);

        // 2) Create UnitLoad containing that LoadItem
        UnitLoad ul1 = unitLoadService.createUnitLoad("T1",null,null,List.of(item));
        UnitLoad ul2 = unitLoadService.createUnitLoad("T2",null,null,List.of(item));
        // 3) Create an available Device
        deviceRepository.save(new Device(true,true,true,new BigDecimal("10.0"),"D1"));

        // 5) Create Shipment containing that unitload (non-terminated)
        shipmentService.create(null, description,"Mialn","Venice", Set.of(ul1,ul2));
    }

    @Transactional
    @And("shipment {string} has trackable {string}")
    public void shipmentHasTrackable(String shipmentDescription, String trackableDescription) {

        Shipment shipment = shipmentService.getAllShipments()
                .stream()
                .filter(s -> shipmentDescription.equals(s.getDescription()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Shipment not found: " + shipmentDescription));

        boolean found = shipment.getTrackables().stream().anyMatch(t -> {
            if (t instanceof UnitLoad ul) {
                return trackableDescription.equals(ul.getDescription());
            }
            if (t instanceof Package p) {
                return trackableDescription.equals(p.getDescription());
            }
            return false;
        });

        assertTrue(found,
                "Shipment " + shipmentDescription + " does not contain trackable with description: " + trackableDescription);
    }

    @And("there is an available device with code {string}")
    public void thereIsAnAvailableDeviceWithCode(String code) {
        assertTrue(deviceRepository.findByOrigin(code).isPresent());
    }

    @When("I open the {string} page for shipment {string}")
    public void iOpenThePageForShipment(String pageName, String shipmentDescription) {

        if (!pageName.equals("Trackables & Devices")) {
            throw new IllegalArgumentException("Unsupported page: " + pageName);
        }

        // 1) vai alla lista spedizioni
        world.driver.get(world.baseUrl + resolveUrl("Shipments list"));
        world.shipmentListPage = new ShipmentListPage(world.driver);

        // 2) trova la shipment tra quelle attive
        ShipmentListPage.ShipmentRow row = null;
        for (ShipmentListPage.ShipmentRow r : world.shipmentListPage.getActiveShipments()) {
            if (shipmentDescription.equals(r.description())) {
                row = r;
                break;
            }
        }

        if (row == null) {
            throw new AssertionError("Active shipment not found in list: " + shipmentDescription);
        }

        // 3) click "Trackables and Devices"
        world.trackablesDevicesPage = row.clickTrackablesAndDevices();
    }

    @And("I bind device {string} to trackable {string}")
    public void iBindDeviceToTrackable(String origin, String trackableDescription) {
        // 1) device id da origin
        Device device = deviceRepository.findByOrigin(origin)
                .orElseThrow(() -> new AssertionError("Device not found with code: " + origin));

        // 2) trackable id da description (UnitLoad o Package)
        Long trackableId = unitLoadRepository.findByDescription(trackableDescription).stream()
                .findFirst()
                .map(UnitLoad::getId)
                .orElseGet(() ->
                        packageRepository.findByDescription(trackableDescription).stream()
                                .findFirst()
                                .map(it.univr.track.entity.data.Package::getId)
                                .orElseThrow(() -> new AssertionError("Trackable not found with description: " + trackableDescription))
                );

        // 3) usa PageObject per eseguire il bind
        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        if (section == null) {
            throw new AssertionError("Trackable section not found on page for trackableId: " + trackableId);
        }

        world.trackablesDevicesPage = section.bindDevice(String.valueOf(device.getId()));


    }

    @Then("trackable {string} should show device {string} as bound")
    public void trackableShouldShowDeviceAsBound(String trackableDescription, String origin) {

        Long deviceId = deviceRepository.findByOrigin(origin)
                .orElseThrow(() -> new AssertionError("Device not found with code: " + origin))
                .getId();

        Long trackableId = unitLoadRepository.findByDescription(trackableDescription).stream()
                .findFirst()
                .map(UnitLoad::getId)
                .orElseGet(() ->
                        packageRepository.findByDescription(trackableDescription).stream()
                                .findFirst()
                                .map(it.univr.track.entity.data.Package::getId)
                                .orElseThrow(() -> new AssertionError("Trackable not found: " + trackableDescription))
                );

        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        Assertions.assertNotNull(section, "Trackable section not found for id: " + trackableId);

        Assertions.assertTrue(
                section.containsBoundDeviceId(String.valueOf(deviceId)),
                "Device " + origin + " (id=" + deviceId + ") is not shown as bound"
        );
    }


    @And("device {string} should not be in the list of available devices")
    public void deviceShouldNotBeInTheListOfAvailableDevices(String origin) {
        Device device = deviceRepository.findByOrigin(origin)
                .orElseThrow(() -> new AssertionError("Device not found with code: " + origin));

        Long deviceId = device.getId();

        boolean foundAsAvailable = world.trackablesDevicesPage.trackableSections.stream()
                .map(sectionElement ->
                        world.trackablesDevicesPage.new TrackableSection(sectionElement))
                .flatMap(section -> section.getAvailableDeviceIds().stream())
                .anyMatch(id -> id.equals(deviceId));

        Assertions.assertFalse(
                foundAsAvailable,
                "Device " + origin + " (id=" + deviceId + ") is still shown as available"
        );
    }

    //# Scenario 7

    @Transactional
    @And("device {string} is bound to trackable {string} in shipment {string}")
    public void deviceIsBoundToTrackableInShipment(String origin, String trackableDescription, String shipmentDescription) {
        // 1) shipment esiste?
        Shipment shipment = shipmentService.getAllShipments().stream()
                .filter(s -> shipmentDescription.equals(s.getDescription()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Shipment not found: " + shipmentDescription));

        // 2) trackable id (UnitLoad o Package) da description
        Long trackableId = unitLoadRepository.findByDescription(trackableDescription).stream()
                .findFirst()
                .map(UnitLoad::getId)
                .orElseGet(() ->
                        packageRepository.findByDescription(trackableDescription).stream()
                                .findFirst()
                                .map(it.univr.track.entity.data.Package::getId)
                                .orElseThrow(() -> new AssertionError("Trackable not found: " + trackableDescription))
                );

        // controlla che il trackable sia nella shipment
        boolean presentInShipment = shipment.getTrackables().stream().anyMatch(t -> {
            if (t instanceof UnitLoad ul) return ul.getId().equals(trackableId);
            if (t instanceof it.univr.track.entity.data.Package p) return p.getId().equals(trackableId);
            return false;
        });
        if (!presentInShipment) {
            throw new AssertionError("Trackable '" + trackableDescription + "' is not part of shipment '" + shipmentDescription + "'");
        }

        // 3) device id da code
        Device device = deviceRepository.findByOrigin(origin)
                .orElseThrow(() -> new AssertionError("Device not found with code: " + origin));

        // 4) bind
        deviceAssignmentService.bind(device.getId(), trackableId);
    }

    @Transactional
    @And("I unbind device {string} from trackable {string}")
    public void iUnbindDeviceFromTrackable(String origin, String trackableDescription) {

        Long deviceId = deviceRepository.findByOrigin(origin)
                .orElseThrow(() -> new AssertionError("Device not found with code: " + origin))
                .getId();

        Long trackableId = unitLoadRepository.findByDescription(trackableDescription).stream()
                .findFirst()
                .map(UnitLoad::getId)
                .orElseGet(() ->
                        packageRepository.findByDescription(trackableDescription).stream()
                                .findFirst()
                                .map(it.univr.track.entity.data.Package::getId)
                                .orElseThrow(() -> new AssertionError("Trackable not found: " + trackableDescription))
                );

        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        Assertions.assertNotNull(section, "Trackable section not found for id: " + trackableId);

        world.trackablesDevicesPage = section.unbindDevice(String.valueOf(deviceId));
    }

    @Transactional
    @Then("trackable {string} should not show any bound device")
    public void trackableShouldNotShowAnyBoundDevice(String trackableDescription) {

        Long trackableId = unitLoadRepository.findByDescription(trackableDescription).stream()
                .findFirst()
                .map(UnitLoad::getId)
                .orElseGet(() ->
                        packageRepository.findByDescription(trackableDescription).stream()
                                .findFirst()
                                .map(it.univr.track.entity.data.Package::getId)
                                .orElseThrow(() -> new AssertionError("Trackable not found: " + trackableDescription))
                );

        TrackablesDevicesPage.TrackableSection section =
                world.trackablesDevicesPage.getTrackableSectionById(trackableId);

        Assertions.assertNotNull(section, "Trackable section not found for id: " + trackableId);

        // nessun bound device = non esiste nessuna riga con td[1] == deviceId
        // quindi possiamo usare direttamente la tabella: se non hai id specifico, controlliamo che non ci siano righe dati
        boolean hasAnyBound = section.tableDevices.findElements(By.xpath(".//tbody/tr[td]")).stream()
                .anyMatch(tr -> tr.findElements(By.tagName("td")).size() == 7);

        Assertions.assertFalse(hasAnyBound, "Expected no bound devices for trackable " + trackableDescription);
    }

    @Transactional
    @And("device {string} should be in the list of available devices")
    public void deviceShouldBeInTheListOfAvailableDevices(String origin) {

        Long deviceId = deviceRepository.findByOrigin(origin)
                .orElseThrow(() -> new AssertionError("Device not found with code: " + origin))
                .getId();

        boolean found = world.trackablesDevicesPage.trackableSections.stream()
                .map(sectionEl -> world.trackablesDevicesPage.new TrackableSection(sectionEl))
                .anyMatch(section -> section.getAvailableDeviceIds().contains(deviceId));

        Assertions.assertTrue(found, "Device " + origin + " (id=" + deviceId + ") is not shown as available");
    }


}
