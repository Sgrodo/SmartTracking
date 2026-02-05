package it.univr.track.entity.data;

import it.univr.track.BaseTest;
import it.univr.track.entity.sensor.HumidityConstraint;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitLoadTest extends BaseTest {

    @Test
    public void testProducts() {
        UnitLoad ul = new UnitLoad("test unitload",
                ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
        ul.getLoadItems().add(this.loadItems[0]);
        assertEquals(1, ul.getLoadItems().size());

        ul.addLoadItem(this.loadItems[1]);
        assertEquals(2, ul.getLoadItems().size());

        ul.removeProductByQrCode(this.loadItems[0].getQrCode());
        assertEquals(1, ul.getLoadItems().size());

        //unitload should have one LoadItem
        assertTrue(ul.hasProducts());

        ul.removeProductByQrCode(this.loadItems[1].getQrCode());

        assertThrows(IllegalArgumentException.class, () -> {
            ul.addLoadItem(new LoadItem("Anormaly_LoadItem","","this load item is not valid"));
        });

    }

    @Test
    public void testPhysicalConstraints() {
        UnitLoad ul = new UnitLoad("test unitload",
                ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));

        assertEquals(1, ul.getPhysicalConstraints().size());

        ul.addPhysicalConstraint(new HumidityConstraint(50.0));
        assertEquals(2, ul.getPhysicalConstraints().size());

        ul.removePhysicalConstraint(new HumidityConstraint(20.0));
        assertEquals(1, ul.getPhysicalConstraints().size());
    }

    @Test
    public void testFields(){
        ZonedDateTime startDate = ZonedDateTime.now();
        UnitLoad ul = new UnitLoad("description test",
                startDate, null);
        assertEquals("description test", ul.getDescription());
        assertEquals(startDate, ul.getStartDate());

        LoadItem l = loadItems[0];
        assertEquals("Item1", l.getName());
        assertEquals("First Load Item", l.getDescription());
    }

    @Test
    public void testPackageAssociation(){
        UnitLoad ul = new UnitLoad();
        assertNull(ul.getPkg());
        Package p = new Package("PKG001");
        ul.setPackage(p);
        assertEquals(p, ul.getPkg());
        p.addUnitLoad(ul);
        assertTrue(p.getUnitLoads().contains(ul));

    }
}
