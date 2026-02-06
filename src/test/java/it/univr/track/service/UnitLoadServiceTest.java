package it.univr.track.service;

import io.cucumber.java.hu.De;
import it.univr.track.BaseTest;
import it.univr.track.entity.Device;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitLoadServiceTest extends BaseTest {

    @Autowired
    private DeviceService deviceService;


    private UnitLoad utSample1;
    private UnitLoad utSample2;

    private Package pkgSample;

    @Autowired
    private UnitLoadService unitLoadService;
    @Autowired
    private DeviceAssignmentService deviceAssignmentService;

    @BeforeEach
    public void initShipmentDb() {
        unitLoadRepository.deleteAll();
        utSample1 = new UnitLoad("banane", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
        utSample1 = unitLoadRepository.save(utSample1);
        utSample1.addLoadItem(this.loadItems[0]);
        utSample1 = unitLoadRepository.save(utSample1);

        utSample2 = new UnitLoad("mele", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)));
        utSample2 = unitLoadRepository.save(utSample2);
        utSample2.addLoadItem(this.loadItems[1]);
        utSample2 = unitLoadRepository.save(utSample2);

        pkgSample = new Package("frutta", "FRUTTA2024");
        pkgSample = packageRepository.save(pkgSample);
        pkgSample.addUnitLoad(utSample1);
        pkgSample.addUnitLoad(utSample2);
        pkgSample = packageRepository.save(pkgSample);
    }

    @AfterEach
    public void cleanShipmentDb() {
        super.cleanBaseDb();
    }

    @Transactional
    @Test
    public void testBasics(){

        assertThrows(InvalidDataException.class, ()->{
           unitLoadService.getUnitLoadById(null);
        });

        assertThrows(InvalidDataException.class, ()->{
            unitLoadService.deleteUnitLoadById(null);
        });

        assertEquals(2, List.of(unitLoadService.getAllUnitLoads()).size());

    }

    @Transactional
    @Test
    public void testDevicesOfUnitloads(){
        assertEquals(0, unitLoadService.getActiveDevicesOfUnitLoad(utSample1.getId()).size());
        Device d = deviceRepository.save(new Device(
                true,true,true,new BigDecimal("1.0"),"original"));
        deviceAssignmentService.bind(d.getId(),utSample1.getId());
        assertEquals(1, unitLoadService.getActiveDevicesOfUnitLoad(utSample1.getId()).size());
    }

    @Transactional
    @Test
    public void testAddLoadItem(){
        LoadItem l = loadItemRepository.save(new LoadItem("Ciliegie", "QrCode1.0","ciliegie venete"));
        unitLoadService.addLoadItem(utSample1.getId(),l.getId());

        assertThrows(NotFoundException.class, () ->{
           unitLoadService.addLoadItem(utSample1.getId(),79L);
        });

        assertTrue(unitLoadService.removeLoadItemByQrCode(utSample1.getId(),"QrCode1.0"));

        assertFalse(unitLoadService.removeLoadItemByQrCode(utSample1.getId(),"QrCode1.0"));


    }


}
