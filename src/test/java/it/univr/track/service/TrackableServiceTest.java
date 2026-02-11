package it.univr.track.service;

import it.univr.track.BaseTest;
import it.univr.track.entity.Device;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.exception.NotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TrackableServiceTest extends BaseTest {

    @Autowired
    private TrackableService trackableService;
    @Autowired
    private DeviceAssignmentService deviceAssignmentService;

    private UnitLoad ul1;
    private UnitLoad ul2;
    private Package pkg;
    private Shipment shipment;
    @Autowired
    private ShipmentService shipmentService;


    @BeforeEach
    public void initTrackableDb() {
        // unit loads
        ul1 = new UnitLoad("UL1", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
        ul1 = unitLoadRepository.save(ul1);
        ul1.addLoadItem(this.loadItems[0]);
        ul1 = unitLoadRepository.save(ul1);

        ul2 = new UnitLoad("UL2", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)));
        ul2 = unitLoadRepository.save(ul2);
        ul2.addLoadItem(this.loadItems[1]);
        ul2 = unitLoadRepository.save(ul2);

        // package (Trackable) che contiene le unit loads
        pkg = new Package("PKG", "PKG2026");
        pkg = packageRepository.save(pkg);
        pkg.addUnitLoad(ul1);
        pkg.addUnitLoad(ul2);
        pkg = packageRepository.save(pkg);

        // shipment per test setShipment
        shipment = new Shipment("Shipment test", "from", "to");
        shipment = shipmentRepository.save(shipment);
    }

    @AfterEach
    public void cleanTrackableDb() {
        super.cleanBaseDb();
    }

    @Transactional
    @Test
    public void testGetUnitLoadsFromTrackableIds() {
        // passo come "trackable" un package: deve tornare ul1 + ul2
        Set<UnitLoad> result = trackableService.getUnitLoadsFromTrackableIds(Set.of(pkg.getId()));
        assertNotNull(result);
        assertEquals(2, result.size());

        Set<Long> resultIds = result.stream().map(UnitLoad::getId).collect(java.util.stream.Collectors.toSet());
        assertTrue(resultIds.contains(ul1.getId()));
        assertTrue(resultIds.contains(ul2.getId()));
    }

    @Transactional
    @Test
    public void testGetAllTrackablesById() {
        Set<Trackable> trackables = trackableService.getAllTrackablesById(Set.of(pkg.getId(), ul1.getId()));
        assertNotNull(trackables);
        assertEquals(2, trackables.size());

        Set<Long> ids = trackables.stream().map(Trackable::getId).collect(java.util.stream.Collectors.toSet());
        assertTrue(ids.contains(pkg.getId()));
        assertTrue(ids.contains(ul1.getId()));
    }

    @Transactional
    @Test
    public void testGetById() {
        Trackable fetched = trackableService.getById(pkg.getId());
        assertNotNull(fetched);
        assertEquals(pkg.getId(), fetched.getId());

        assertThrows(NotFoundException.class, () -> {
            trackableService.getById(999999L);
        });
    }


    @Test
     public void testDeleteById_expectedBehavior() {
         boolean ok = trackableService.deleteById(pkg.getId());
         assertTrue(ok);
         assertFalse(trackableRepository.existsById(pkg.getId()));

         assertThrows(NotFoundException.class, () ->{
             trackableService.deleteById(789L);
         });
     }

    @Transactional
    @Test
    public void testSetShipment() {
        UnitLoad reloadedUl1 = unitLoadRepository.findById(ul1.getId()).orElseThrow();

        trackableService.setShipment(ul1.getId(), shipment.getId());

        UnitLoad after = unitLoadRepository.findById(ul1.getId()).orElseThrow();
        assertNotNull(after.getShipment());
        assertEquals(shipment.getId(), after.getShipment().getId());

        assertThrows(NotFoundException.class, () -> {
            trackableService.setShipment(89L,1L);
        });
    }

    @Transactional
    @Test
    public void testFreeTrackablesFromShipments(){
        Device d = deviceRepository.save(new Device(true,true,true,new BigDecimal("10.0"),"Napoli"));
        Device d1 = deviceRepository.save(new Device(true,true,true,new BigDecimal("10.0"),"Napoli"));

        deviceAssignmentService.bind(d.getId(),ul1.getId());
        deviceAssignmentService.bind(d1.getId(),pkg.getId());

        Shipment s = shipmentService.create(null,"less", "from", "to", new HashSet<>(Set.of(ul1,pkg)));
        trackableService.freeTrackablesFromShipments(s.getId());

        assertNull(ul1.getShipment());
        assertNull(pkg.getShipment());
    }
}
