package it.univr.track.service;

import it.univr.track.BaseTest;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PackageServiceTest extends BaseTest {

    @Autowired
    private PackageService packageService;

    @Autowired
    private UnitLoadService unitLoadService;

    private UnitLoad ul1;
    private UnitLoad ul2;
    private Package pkg;

    @BeforeEach
    public void initPackageDb() {
        ul1 = new UnitLoad("UL1", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
        ul1 = unitLoadRepository.save(ul1);
        ul1.addLoadItem(this.loadItems[0]);
        ul1 = unitLoadRepository.save(ul1);

        ul2 = new UnitLoad("UL2", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)));
        ul2 = unitLoadRepository.save(ul2);
        ul2.addLoadItem(this.loadItems[1]);
        ul2 = unitLoadRepository.save(ul2);

        pkg = new Package("PKG_DESCRIPTION", "GROUP2026");
        pkg = packageRepository.save(pkg);
    }

    @AfterEach
    public void cleanPackageDb() {
        super.cleanBaseDb();
    }


    @Transactional
    @Test
    public void testGetPackageById() {
        Package fetched = packageService.getPackageById(pkg.getId());
        assertNotNull(fetched);
        assertEquals(pkg.getId(), fetched.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            packageService.getPackageById(null);
        });

        assertThrows(NotFoundException.class, () -> {
            packageService.getPackageById(999999L);
        });
    }

    @Test
    public void testDeletePackageById() {
        assertThrows(InvalidDataException.class, () -> {
            packageService.deletePackageById(null);
        });

        assertThrows(NotFoundException.class, () -> {
            packageService.deletePackageById(999999L);
        });

        boolean result = packageService.deletePackageById(pkg.getId());
        assertTrue(result);

        assertThrows(NotFoundException.class, () -> {
            packageService.getPackageById(pkg.getId());
        });
    }

    @Transactional
    @Test
    public void testAddUnitLoadToPackage() {
        assertThrows(InvalidDataException.class, () -> {
            packageService.addUnitLoadToPackage(null, ul1.getId());
        });

        assertThrows(InvalidDataException.class, () -> {
            packageService.addUnitLoadToPackage(pkg.getId(), null);
        });

        UnitLoad before = unitLoadService.getUnitLoadById(ul1.getId());

        packageService.addUnitLoadToPackage(pkg.getId(), ul1.getId());

        // ricarico e verifico relazione
        Package reloadedPkg = packageService.getPackageById(pkg.getId());
        Set<Long> unitLoadIds = reloadedPkg.getUnitLoads().stream().map(UnitLoad::getId).collect(Collectors.toSet());
        assertTrue(unitLoadIds.contains(ul1.getId()));

        UnitLoad after = unitLoadService.getUnitLoadById(ul1.getId());
        assertNotNull(after.getPkg());
        assertEquals(pkg.getId(), after.getPkg().getId());
    }

    @Transactional
    @Test
    public void testRemoveUnitLoadFromPackage() {

        packageService.addUnitLoadToPackage(pkg.getId(), ul1.getId());


        packageService.removeUnitLoadFromPackage(pkg.getId(), ul1.getId());

        Package reloadedPkg = packageService.getPackageById(pkg.getId());
        Set<Long> ids = reloadedPkg.getUnitLoads().stream().map(UnitLoad::getId).collect(Collectors.toSet());
        assertFalse(ids.contains(ul1.getId()));

        UnitLoad after = unitLoadService.getUnitLoadById(ul1.getId());

        assertTrue(after.getPkg() == null || !after.getPkg().getId().equals(pkg.getId()));
    }

    @Transactional
    @Test
    public void testRemoveUnitLoadFromPackage_shouldThrowWhenUnitLoadNotInPackage() {
        assertThrows(NotFoundException.class, () -> {
            packageService.removeUnitLoadFromPackage(pkg.getId(), ul2.getId());
        });
    }

    @Transactional
    @Test
    public void testGetUnitLoadsOfPackage() {
        packageService.addUnitLoadToPackage(pkg.getId(), ul1.getId());
        packageService.addUnitLoadToPackage(pkg.getId(), ul2.getId());

        Set<UnitLoad> uls = packageService.getUnitLoadsOfPackage(pkg.getId());
        assertNotNull(uls);
        assertEquals(2, uls.size());

        Set<Long> ids = uls.stream().map(UnitLoad::getId).collect(Collectors.toSet());
        assertTrue(ids.contains(ul1.getId()));
        assertTrue(ids.contains(ul2.getId()));
    }


    @Transactional
    @Test
    public void testCreatePackage() {
        assertThrows(InvalidDataException.class, () -> {
            packageService.createPackage("GC", "DESC", null);
        });

        assertThrows(InvalidDataException.class, () -> {
            packageService.createPackage("GC", "DESC", Set.of());
        });

        Package created = packageService.createPackage("GC2026", "NEW_PKG", Set.of(ul1, ul2));
        assertNotNull(created);
        assertNotNull(created.getId());

        Package fetched = packageService.getPackageById(created.getId());
        assertEquals("GC2026", fetched.getGroupCode()); // se hai getter groupCode
        assertEquals(2, fetched.getUnitLoads().size());

        Set<Long> ids = fetched.getUnitLoads().stream().map(UnitLoad::getId).collect(Collectors.toSet());
        assertTrue(ids.contains(ul1.getId()));
        assertTrue(ids.contains(ul2.getId()));

        UnitLoad after1 = unitLoadService.getUnitLoadById(ul1.getId());
        UnitLoad after2 = unitLoadService.getUnitLoadById(ul2.getId());
        assertNotNull(after1.getPkg());
        assertNotNull(after2.getPkg());
        assertEquals(created.getId(), after1.getPkg().getId());
        assertEquals(created.getId(), after2.getPkg().getId());
    }

    @Test
    public void testGetAllPackages() {

        Set<Package> pkgs = packageService.getAllPackages();
        assertNotNull(pkgs);
        assertFalse(pkgs.isEmpty());

        Set<Long> ids = pkgs.stream().map(Package::getId).collect(Collectors.toSet());
        assertTrue(ids.contains(pkg.getId()));
    }
}
