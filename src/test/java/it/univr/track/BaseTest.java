package it.univr.track;


import it.univr.track.entity.Device;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class BaseTest {

    protected long[] loadItemsIds;
    protected long[] deviceIds;

    @Autowired
    private LoadItemRepository loadItemRepository;

    @Autowired
    private UnitLoadRepository unitLoadRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeEach
    public void initDb(){
        initProducts();
        initDevices();
    }

    private void initProducts() {
        LoadItem loadItem1 = new LoadItem("Item1", "QR001", "First Load Item");
        LoadItem loadItem2 = new LoadItem("Item2", "QR002", "Second Load Item");
        loadItemRepository.save(loadItem1);
        loadItemRepository.save(loadItem2);

        loadItemsIds = new long[]{loadItem1.getId(), loadItem2.getId()};
    }

    private void initDevices() {
        // Implement device initialization if needed
        Device device1 = new Device(true, true, true, new BigDecimal(10), "Origin1");
        Device device2 = new Device(false, true, false, new BigDecimal(20), "Origin2");
        deviceRepository.save(device1);
        deviceRepository.save(device2);

        deviceIds = new long[]{device1.getId(), device2.getId()};
    }

    @AfterEach
    public void cleanDb(){
        deviceRepository.deleteAll();
        unitLoadRepository.deleteAll();
        shipmentRepository.deleteAll();
        packageRepository.deleteAll();
        loadItemRepository.deleteAll();
    }

}
