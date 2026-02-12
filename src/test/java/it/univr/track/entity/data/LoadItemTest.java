package it.univr.track.entity.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadItemTest {

    // --- VALID CASES ---

    @Test
    void constructor_shouldCreateLoadItem_whenAllRequiredFieldsValid_andDescriptionNull() {
        LoadItem li = new LoadItem("Milk", "QR-123", null);

        assertEquals("Milk", li.getName());
        assertEquals("QR-123", li.getQrCode());
        assertNull(li.getDescription());

        assertNotNull(li.getUnitLoads());
        assertTrue(li.getUnitLoads().isEmpty());
    }

    @Test
    void constructor_shouldCreateLoadItem_whenDescriptionProvided() {
        LoadItem li = new LoadItem("Vaccine", "QR-999", "Keep refrigerated");

        assertEquals("Vaccine", li.getName());
        assertEquals("QR-999", li.getQrCode());
        assertEquals("Keep refrigerated", li.getDescription());
    }

    @Test
    void unitLoadsGetter_shouldReturnLiveSet_backedByEntityField() {
        LoadItem li = new LoadItem("Box", "QR-1", "desc");

        assertEquals(0, li.getUnitLoads().size());
        li.getUnitLoads().add(null); // Set accetta null; è ok come test "live"
        assertEquals(1, li.getUnitLoads().size());
    }


    @Test
    void constructor_shouldThrow_whenNameIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new LoadItem(null, "QR-123", "desc")
        );
        assertEquals("name is blank", ex.getMessage());
    }

    @Test
    void constructor_shouldThrow_whenNameIsBlank_emptyString() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new LoadItem("", "QR-123", "desc")
        );
        assertEquals("name is blank", ex.getMessage());
    }

    @Test
    void constructor_shouldThrow_whenNameIsBlank_whitespace() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new LoadItem("   \n\t  ", "QR-123", "desc")
        );
        assertEquals("name is blank", ex.getMessage());
    }


    @Test
    void constructor_shouldThrow_whenQrCodeIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new LoadItem("Milk", null, "desc")
        );
        assertEquals("qrCode is blank", ex.getMessage());
    }

    @Test
    void constructor_shouldThrow_whenQrCodeIsBlank_emptyString() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new LoadItem("Milk", "", "desc")
        );
        assertEquals("qrCode is blank", ex.getMessage());
    }

    @Test
    void constructor_shouldThrow_whenQrCodeIsBlank_whitespace() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new LoadItem("Milk", "   \n\t ", "desc")
        );
        assertEquals("qrCode is blank", ex.getMessage());
    }


    @Test
    void constructor_shouldAcceptNameAndQrCodeWithLeadingTrailingSpaces_ifNotBlank() {
        LoadItem li = new LoadItem("  Product  ", "  QR-X  ", "d");

        assertEquals("  Product  ", li.getName());
        assertEquals("  QR-X  ", li.getQrCode());
    }
}
