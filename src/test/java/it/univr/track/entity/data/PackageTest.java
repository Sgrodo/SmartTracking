package it.univr.track.entity.data;

import it.univr.track.BaseTest;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.repository.PackageRepository;
import it.univr.track.repository.UnitLoadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PackageTest extends BaseTest {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private UnitLoadRepository unitLoadRepository;

    private UnitLoad utSample1;
    private UnitLoad utSample2;

    @BeforeEach
    public void initShipmentDb() {
        utSample1 = new UnitLoad("banane", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
        utSample2 = new UnitLoad("mele", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)));

        utSample1 = unitLoadRepository.save(utSample1);
        utSample2 = unitLoadRepository.save(utSample2);

        utSample1.addLoadItem(this.loadItems[0]);
        utSample2.addLoadItem(this.loadItems[1]);

        utSample1 = unitLoadRepository.save(utSample1);
        utSample2 = unitLoadRepository.save(utSample2);
    }

    @AfterEach
    public void resetShipmentDb() {
        super.cleanBaseDb();
    }

    @Test
    public void testPackageCollection() {
        Package pkg = new Package("Frutta");
        pkg = packageRepository.save(pkg);

        assertEquals(0, pkg.getUnitLoads().size());

        pkg.addUnitLoad(utSample1);
        pkg.addUnitLoad(utSample2);

        assertEquals(2, pkg.getUnitLoads().size());

        pkg.removeUnitLoad(utSample1);
        assertEquals(1, pkg.getUnitLoads().size());
    }

    @Test
    public void testFields(){
        Package pkg = new Package("Trasporto di Frutta per Supermercato Lidl","Frutta");
        pkg = packageRepository.save(pkg);
        assertEquals("Trasporto di Frutta per Supermercato Lidl", pkg.getDescription());
        assertEquals("Frutta", pkg.getGroupCode());
        assertThrows(IllegalArgumentException.class, ()->{
            Package pkg1 = new Package("Trasporto di Frutta per Supermercato Lidl","");
        });

        assertThrows(IllegalArgumentException.class, ()->{
            Package pkg2 = new Package("Trasporto di Frutta per Supermercato Lidl",null);
        });

    }
}
