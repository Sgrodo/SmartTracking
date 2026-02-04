package it.univr.track.entity.data;

import it.univr.track.BaseTest;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitLoadTest extends BaseTest {

    @Test
    public void testConstructor() {
        // test constructor without load items
        assertThrows(
                InvalidDataException.class,
                () -> new UnitLoad("unvalid unitload",
                        ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)), null)
        );
    }

    @Test
    public void testProducts() {
        UnitLoad ul = new UnitLoad("test unitload",
                ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)), List.of(this.loadItems[0]));
        assertArrayEquals(new LoadItem[]{this.loadItems[0]}, ul.getProducts().toArray());

        ul.addProduct(this.loadItems[1]);
        assertArrayEquals(new LoadItem[]{this.loadItems[0], this.loadItems[1]}, ul.getProducts().toArray());

        ul.removeProductByQrCode(this.loadItems[0].getQrCode());
        assertArrayEquals(new LoadItem[]{this.loadItems[1]}, ul.getProducts().toArray());
    }

    @Test
    public void testPhysicalConstraints() {
        UnitLoad ul = new UnitLoad("test unitload",
                ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)), List.of(this.loadItems[0]));

        assertEquals(1, ul.getPhysicalConstraints().size());

        ul.addPhysicalConstraint(new HumidityConstraint(50.0));
        assertEquals(2, ul.getPhysicalConstraints().size());

        ul.removePhysicalConstraint(new HumidityConstraint(20.0));
        assertEquals(1, ul.getPhysicalConstraints().size());
    }


}
