package it.univr.track.entity;

import it.univr.track.BaseTest;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.exception.InvalidDataException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceAssignmentTest extends BaseTest {

    private UnitLoad ul1;
    private UnitLoad ul2;
    @BeforeEach
    public void setUp() {
        ul1 = new UnitLoad();
        ul2 = new UnitLoad();

        unitLoadRepository.save(ul1);
        unitLoadRepository.save(ul2);

        ul1.addLoadItem(loadItems[0]);
        ul2.addLoadItem(loadItems[1]);

        unitLoadRepository.saveAndFlush(ul1);
        unitLoadRepository.saveAndFlush(ul2);

    }

    @AfterEach
    public void tearDown() {
        super.cleanBaseDb();
    }


    @Test
    public void testDeviceAssignmentCreation() {

        assertThrows(InvalidDataException.class, ()-> {
            DeviceAssignment assignment = new DeviceAssignment(null, ul1);
        });

        assertThrows(InvalidDataException.class, ()-> {
            DeviceAssignment assignment = new DeviceAssignment(devices[0], null);
        });

        DeviceAssignment assignment = new DeviceAssignment(devices[0], ul1 );

    }

    @Test
    public void testDeviceAssignmentDeactivation() {
        DeviceAssignment assignment = new DeviceAssignment(devices[0], ul1 );
        assertTrue(assignment.isActive());

        assignment.deactivate(null);
        assertFalse(assignment.isActive());

        // Deactivating an already inactive assignment should have no effect
        assignment.deactivate();
        assertFalse(assignment.isActive());
    }

    @Test
    public void testFields() {
        DeviceAssignment assignment = new DeviceAssignment(devices[0], ul1 );
        assertEquals(devices[0], assignment.getDevice());
        assertEquals(ul1, assignment.getTarget());
        assertNotNull(assignment.getFromTs());
        assertNull(assignment.getToTs());
    }
}
