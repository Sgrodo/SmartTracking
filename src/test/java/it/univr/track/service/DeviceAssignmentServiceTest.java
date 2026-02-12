package it.univr.track.service;

import it.univr.track.BaseTest;
import it.univr.track.entity.Device;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.sensor.HumidityConstraint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class DeviceAssignmentServiceTest extends BaseTest {

    @Autowired
    private DeviceAssignmentService deviceAssignmentService;

    @Autowired
    private TrackableService trackableService;

    private Device device1;
    private Device device2;
    private UnitLoad ul1;
    private UnitLoad ul2;

    @BeforeEach
    public void initDb() {
        ul1 = new UnitLoad("UL1", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
        ul1 = unitLoadRepository.save(ul1);
        ul1.addLoadItem(this.loadItems[0]);
        ul1 = unitLoadRepository.save(ul1);

        ul2 = new UnitLoad("UL2", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)));
        ul2 = unitLoadRepository.save(ul2);
        ul2.addLoadItem(this.loadItems[1]);
        ul2 = unitLoadRepository.save(ul2);

        device1 = new Device(true, true, true, new BigDecimal("1.0"), "Italy");
        device2 = new Device(true, true, true, new BigDecimal("2.0"), "Germany");
        device1 = deviceRepository.save(device1);
        device2 = deviceRepository.save(device2);
    }

    @AfterEach
    public void cleanDb() {
        super.cleanBaseDb();
    }



}
