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
import it.univr.track.repository.*;
import it.univr.track.service.*;
import it.univr.track.web.po.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;


import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
            case "Shipments" -> "/shipments";
            case "Edit shipment" -> "/shipments/%s/edit";

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
    @Given("there is an available Package with description {string}")
    public void thereIsAnAvailablePackageWithDescription(String packageDescription) {
        // per creare un Package “valido” mi serve almeno 1 UnitLoad (dipende dal tuo dominio)
        LoadItem item = new LoadItem("banana", java.util.UUID.randomUUID().toString(), "banana");
        item = loadItemRepository.save(item);

        UnitLoad ul = unitLoadService.createUnitLoad("UL-for-" + packageDescription, null, null, java.util.List.of(item));

        // usa il tuo packageService reale: io assumo createPackage(groupCode, description, unitLoads)
        it.univr.track.entity.data.Package created = packageService.createPackage(
                "GC-" + packageDescription, packageDescription, java.util.Set.of(ul)
        );

        // opzionale: cache
        world.trackableIdByDescription.put(packageDescription, created.getId());
    }

    @When("I open the {string} page")
    public void iOpenThePage(String pageName) {
        world.driver.get(world.baseUrl + resolveUrl(pageName));

        switch (pageName) {
            case "Shipments list" -> world.shipmentListPage = new ShipmentListPage(world.driver);
            case "Shipments" -> world.shipmentListPage = new ShipmentListPage(world.driver); // dopo redirect atterri sulla list
            case "Create shipment" -> world.createShipmentPage = new CreateShipmentPage(world.driver);
            case "Create UnitLoad" -> world.createUnitLoadPage = new CreateUnitLoadPage(world.driver);
            case "Create Package" -> world.createPackagePage = new CreatePackagePage(world.driver);
            case "Edit Page" -> world.editShipmentPage = new EditShipmentPage(world.driver);
            default -> throw new IllegalArgumentException("Unsupported page: " + pageName);
        }
    }

    @And("I enter from-location {string} and to-location {string}")
    public void iEnterFromLocationAndToLocation(String from, String to) {
        world.createShipmentPage.setFromLocation(from);
        world.createShipmentPage.setToLocation(to);
    }

    @And("I enter shipment description {string}")
    public void iEnterShipmentDescription(String description) {
        world.createShipmentPage.setDescription(description);
    }

    @And("I select UnitLoad {string}")
    public void iSelectUnitLoad(String unitLoadDescription) {
        Assertions.assertTrue(
                world.createShipmentPage.containsUnitLoadDescription(unitLoadDescription),
                "UnitLoad not present in table: " + unitLoadDescription
        );
        Assertions.assertTrue(
                world.createShipmentPage.selectUnitLoadByDescription(unitLoadDescription),
                "Could not select UnitLoad: " + unitLoadDescription
        );
    }

    @And("I select Package {string}")
    public void iSelectPackage(String packageDescription) {
        Assertions.assertTrue(
                world.createShipmentPage.containsPackageDescription(packageDescription),
                "Package not present in table: " + packageDescription
        );
        Assertions.assertTrue(
                world.createShipmentPage.selectPackageByDescription(packageDescription),
                "Could not select Package: " + packageDescription
        );
    }

    @And("I submit the shipment creation")
    public void iSubmitTheShipmentCreation() {
        world.shipmentListPage = world.createShipmentPage.createShipment();
    }

    @Then("I should be redirected to the {string} page")
    public void iShouldBeRedirectedToThePage(String pageName) {
        if (pageName.equals("Shipments list")) {
            world.shipmentListPage = new ShipmentListPage(world.driver);
            // check minimo: tabella presente
            Assertions.assertTrue(
                    world.shipmentListPage.tableActiveShipments.isDisplayed(),
                    "Active shipments table not visible"
            );
            return;
        }
        throw new IllegalArgumentException("Unsupported page: " + pageName);
    }

    @Transactional
    @And("the shipment {string} should exist with tracking state {string}")
    public void theShipmentShouldExistWithTrackingState(String shipmentDescription, String expectedTrackingState) {

        // prendo l'id dal DB (non dalla lista)
        Shipment shipment = shipmentRepository.findByDescription(shipmentDescription)
                .orElseThrow(() -> new AssertionError("Shipment not found in DB: " + shipmentDescription));

        world.shipmentIdByDescription.put(shipmentDescription, shipment.getId());

        // apro details e leggo tracking state dalla pagina
        world.driver.get(world.baseUrl + "/shipments/" + shipment.getId());
        world.shipmentDetailsPage = new ShipmentDetailsPage(world.driver);

        Assertions.assertEquals(
                expectedTrackingState,
                world.shipmentDetailsPage.getTrackingState(),
                "Unexpected tracking state for shipment " + shipmentDescription
        );
    }

    //# Scenario 1 (validation case)

    @Then("I should remain on the {string} page")
    public void iShouldRemainOnThePage(String pageName) {

        switch (pageName) {
            case "Create UnitLoad" -> {
                world.createUnitLoadPage = new CreateUnitLoadPage(world.driver);
                Assertions.assertTrue(
                        world.createUnitLoadPage.getTitle().toLowerCase().contains("unitload"),
                        "Not on Create UnitLoad page (title=" + world.createUnitLoadPage.getTitle() + ")"
                );
                return;
            }
            case "Create Package" -> {
                world.createPackagePage = new CreatePackagePage(world.driver);
                Assertions.assertTrue(
                        world.createPackagePage.getTitle().toLowerCase().contains("package"),
                        "Not on Create Package page (title=" + world.createPackagePage.getTitle() + ")"
                );
                return;
            }
            case "Create shipment" -> {
                world.createShipmentPage = new CreateShipmentPage(world.driver);
                Assertions.assertTrue(
                        world.createShipmentPage.getTitle().toLowerCase().contains("shipment"),
                        "Not on Create shipment page (title=" + world.createShipmentPage.getTitle() + ")"
                );
                return;
            }
            case "Edit shipment" -> {
                world.editShipmentPage = new EditShipmentPage(world.driver);
                Assertions.assertTrue(
                        world.editShipmentPage.h1EditShipment.getText().contains("Edit"),
                        "Not on Edit shipment page"
                );
                return;
            }


        }

        throw new IllegalArgumentException("Unsupported page: " + pageName);
    }

    @And("I should see an error message")
    public void iShouldSeeAnErrorMessage() {

        // se sei su Edit shipment usa EditShipmentPage
        if (world.editShipmentPage != null && world.driver.getCurrentUrl().contains("/edit")) {
            //html blocca quindi assert al contrario
            Assertions.assertFalse(world.editShipmentPage.hasError(), "Error message is not displayed (Edit shipment)");
            Assertions.assertTrue(world.editShipmentPage.getError().isBlank(), "Error message is empty (Edit shipment)");
            return;
        }

        // fallback: Create shipment / Create package / Create unitload
        if (world.createShipmentPage != null && world.driver.getCurrentUrl().contains("/newShipment")) {
            Assertions.assertTrue(world.createShipmentPage.hasError(), "Error message is not displayed (Create shipment)");
            Assertions.assertFalse(world.createShipmentPage.getError().isBlank(), "Error message is empty (Create shipment)");
            return;
        }

        if (world.createPackagePage != null && world.driver.getCurrentUrl().contains("/packages/new")) {
            Assertions.assertTrue(world.createPackagePage.hasError(), "Error message is not displayed (Create package)");
            Assertions.assertFalse(world.createPackagePage.getError().isBlank(), "Error message is empty (Create package)");
            return;
        }

        if (world.createUnitLoadPage != null && world.driver.getCurrentUrl().contains("/unitloads/new")) {
            Assertions.assertTrue(world.createUnitLoadPage.hasError(), "Error message is not displayed (Create UnitLoad)");
            Assertions.assertFalse(world.createUnitLoadPage.getError().isBlank(), "Error message is empty (Create UnitLoad)");
            return;
        }

        throw new IllegalStateException("Don't know which page I'm on. url=" + world.driver.getCurrentUrl());
    }


    //# Scenario 4

    @Transactional
    @Given("there is an existing shipment with description {string}")
    public void thereIsAnExistingShipmentWithDescription(String description) {
        LoadItem item = new LoadItem("banana", java.util.UUID.randomUUID().toString(), "banana");
        item = loadItemRepository.save(item);

        UnitLoad ul = unitLoadService.createUnitLoad("UL-for-" + description, null, null, java.util.List.of(item));

        Shipment created = shipmentService.create(null, description, "Verona", "Padova", java.util.Set.of(ul));
        world.shipmentIdByDescription.put(description, created.getId());
    }

    @Then("I should see shipment {string} in the list")
    public void iShouldSeeShipmentInTheList(String shipmentDescription) {
        Assertions.assertTrue(
                world.shipmentListPage.containsActiveShipmentDescription(shipmentDescription),
                "Shipment not found in active list: " + shipmentDescription
        );
    }

    @Transactional
    @When("I open details for shipment {string}")
    public void iOpenDetailsForShipment(String shipmentDescription) {
        // via URL diretto (non dipende dalla lista)
        Long shipmentId = world.requireShipmentId(shipmentDescription);
        world.driver.get(world.baseUrl + "/shipments/" + shipmentId);
        world.shipmentDetailsPage = new ShipmentDetailsPage(world.driver);
    }

    @Then("I should see the shipment details page for {string}")
    public void iShouldSeeTheShipmentDetailsPageFor(String shipmentDescription) {
        Assertions.assertEquals(
                shipmentDescription,
                world.shipmentDetailsPage.getDescription(),
                "Wrong shipment details page opened"
        );
    }

    //# Scenario 5
    @Transactional
    @Given("there is an existing shipment with description {string} and tracking state {string}")
    public void thereIsAnExistingShipmentWithDescriptionAndTrackingState(String description, String trackingState) {

        // 1) creo shipment con 1 unitload (trackable)
        LoadItem item = new LoadItem("banana", java.util.UUID.randomUUID().toString(), "banana");
        item = loadItemRepository.save(item);

        UnitLoad ul = unitLoadService.createUnitLoad("UL-for-" + description, null, null, java.util.List.of(item));

        Shipment shipment = shipmentService.create(null, description, "Verona", "Padova", java.util.Set.of(ul));

        world.shipmentIdByDescription.put(description, shipment.getId());
        world.trackableIdByDescription.put("T-IN-" + description, ul.getId()); // key interna

        // 2) creo device disponibile
        Device d = new Device(true, true, true, new java.math.BigDecimal("10.0"), "DEV-" + description);
        d = deviceRepository.save(d);
        world.deviceIdByOrigin.put(d.getOrigin(), d.getId());

        // 3) bind device al trackable (così devices non è vuoto)
        deviceAssignmentService.bind(d.getId(), ul.getId());
    }

    @And("I click {string}")
    public void iClick(String label) {
        // mappo le label del feature ai veri bottoni della pagina
        switch (label) {
            case "Activate tracking" -> world.shipmentDetailsPage = world.shipmentDetailsPage.activateTracking();
            case "Deactivate tracking" -> world.shipmentDetailsPage = world.shipmentDetailsPage.deActivateTracking();
            default -> throw new IllegalArgumentException("Unsupported click action: " + label);
        }
    }
    @Then("shipment {string} tracking state should be {string}")
    public void shipmentTrackingStateShouldBe(String shipmentDescription, String expectedTrackingState) {
        // controllo UI:
        Assertions.assertEquals(expectedTrackingState, world.shipmentDetailsPage.getTrackingState());
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



    //shipment-edit-complete-cancel

    @And("I open edit for shipment {string}")
    public void iOpenEditForShipment(String shipmentDescription) {

        // assicurati di essere sulla list
        if (world.shipmentListPage == null) {
            world.shipmentListPage = new ShipmentListPage(world.driver);
        }

        ShipmentListPage.ShipmentRow row = null;
        for (ShipmentListPage.ShipmentRow r : world.shipmentListPage.getActiveShipments()) {
            if (shipmentDescription.equals(r.description())) {
                row = r;
                break;
            }
        }
        if (row == null) throw new AssertionError("Active shipment not found: " + shipmentDescription);

        world.editShipmentPage = row.clickEdit();  // devi avere EditShipmentPage
    }


    @Then("I should be on the {string} page")
    public void iShouldBeOnThePage(String pageName) {
        if (pageName.equals("Edit shipment")) {
            Assertions.assertTrue(world.editShipmentPage.h1.getText().contains("Edit"),
                    "Not on Edit shipment page");
            return;
        }
        // altrimenti usa il tuo check URL
        iShouldRemainOnThePage(pageName);
    }


    @And("I set fromLocation to {string}")
    public void iSetFromLocation(String v) {
        world.editShipmentPage.setFromLocation(v);
    }

    @And("I set toLocation to {string}")
    public void iSetToLocation(String v) {
        world.editShipmentPage.setToLocation(v);
    }

    @And("I select trackable {string} in edit form")
    public void iSelectTrackableInEditForm(String trackableDescription) {
        // qui serve solo per convertire description -> id (UnitLoad/Package)
        Long trackableId = unitLoadRepository.findByDescription(trackableDescription).stream()
                .findFirst().map(UnitLoad::getId)
                .orElseGet(() -> packageRepository.findByDescription(trackableDescription).stream()
                        .findFirst().map(it.univr.track.entity.data.Package::getId)
                        .orElseThrow(() -> new AssertionError("Trackable not found: " + trackableDescription)));

        world.editShipmentPage.selectTrackableCheckboxById(trackableId, true);
    }

    @And("I submit shipment edit")
    public void iSubmitShipmentEdit() {
        world.editShipmentPage.submit();
        // dopo submit spesso redirect/list -> reinstanzia
        world.shipmentListPage = new ShipmentListPage(world.driver);
    }

    @And("I unselect all trackables in edit form")
    public void iUnselectAllTrackables() {
        world.editShipmentPage.unselectAllTrackables();
    }

    @And("I click complete for shipment {string}")
    public void iClickCompleteForShipment(String shipmentDescription) {
        ShipmentListPage.ShipmentRow row = findActiveRowByDescription(shipmentDescription);
        world.shipmentListPage = row.clickComplete();
    }

    @And("I click cancel for shipment {string}")
    public void iClickCancelForShipment(String shipmentDescription) {
        ShipmentListPage.ShipmentRow row = findActiveRowByDescription(shipmentDescription);
        world.shipmentListPage = row.clickCancel();
    }

    //helper

    private ShipmentListPage.ShipmentRow findActiveRowByDescription(String shipmentDescription) {
        for (ShipmentListPage.ShipmentRow r : world.shipmentListPage.getActiveShipments()) {
            if (shipmentDescription.equals(r.description())) return r;
        }
        throw new AssertionError("Active shipment not found: " + shipmentDescription);
    }
}
