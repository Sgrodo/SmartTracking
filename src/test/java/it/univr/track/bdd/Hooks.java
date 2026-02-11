package it.univr.track.bdd;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import it.univr.track.repository.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public class Hooks {
    private final TestWorld world;

    private final DeviceAssignmentRepository deviceAssignmentRepository;
    private final ShipmentRepository shipmentRepository;
    private final PackageRepository packageRepository;
    private final UnitLoadRepository unitLoadRepository;
    private final LoadItemRepository loadItemRepository;
    private final DeviceRepository deviceRepository;
    private final TrackableRepository trackableRepository;

    public Hooks(
            TestWorld world,
            DeviceAssignmentRepository deviceAssignmentRepository,
            ShipmentRepository shipmentRepository,
            PackageRepository packageRepository,
            UnitLoadRepository unitLoadRepository,
            LoadItemRepository loadItemRepository,
            DeviceRepository deviceRepository,
            TrackableRepository trackableRepository
    ) {
        this.world = world;
        this.deviceAssignmentRepository = deviceAssignmentRepository;
        this.shipmentRepository = shipmentRepository;
        this.packageRepository = packageRepository;
        this.unitLoadRepository = unitLoadRepository;
        this.loadItemRepository = loadItemRepository;
        this.deviceRepository = deviceRepository;
        this.trackableRepository = trackableRepository;
    }

    @Before
    public void setUp() {

        cleanDb();

        FirefoxOptions options = new FirefoxOptions();

        world.driver = new FirefoxDriver(options);
        world.driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        if (world.driver != null) {
            world.driver.quit();
        }
    }

    private void cleanDb() {
        deviceAssignmentRepository.deleteAllInBatch(); // figli
        trackableRepository.deleteAllInBatch();        // figli di shipment
        packageRepository.deleteAllInBatch();          // se contiene unitloads
        unitLoadRepository.deleteAllInBatch();
        loadItemRepository.deleteAllInBatch();
        deviceRepository.deleteAllInBatch();
        shipmentRepository.deleteAllInBatch();         // per ultimo (padre)
    }

}
