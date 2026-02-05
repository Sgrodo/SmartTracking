package it.univr.track;


import it.univr.track.entity.Device;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.factory.LoadItemFactory;
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

    protected LoadItem[] loadItems;
    protected Device[] devices;

    @Autowired
    protected LoadItemRepository loadItemRepository;

    @Autowired
    protected UnitLoadRepository unitLoadRepository;

    @Autowired
    protected PackageRepository packageRepository;

    @Autowired
    protected ShipmentRepository shipmentRepository;

    @Autowired
    protected DeviceRepository deviceRepository;

    @BeforeEach
    public void initBaseDb(){
        initProducts();
        initDevices();
    }

    private void initProducts() {
        LoadItem loadItem1 = LoadItemFactory.item1();
        LoadItem loadItem2 = LoadItemFactory.item2();
        loadItem1 = loadItemRepository.save(loadItem1);
        loadItem2 = loadItemRepository.save(loadItem2);

        loadItems = new LoadItem[]{loadItem1, loadItem2};
    }

    private void initDevices() {
        // Implement device initialization if needed
        Device device1 = new Device(true, true, true, new BigDecimal(10), "Origin1");
        Device device2 = new Device(false, true, false, new BigDecimal(20), "Origin2");
        device1 = deviceRepository.save(device1);
        device2 = deviceRepository.save(device2);

        devices = new Device[]{device1, device2};
    }

    @AfterEach
    public void cleanBaseDb(){
        deviceRepository.deleteAll();
        unitLoadRepository.deleteAll();
        shipmentRepository.deleteAll();
        packageRepository.deleteAll();
        loadItemRepository.deleteAll();
    }

}
