package it.univr.track.service;

import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.repository.PackageRepository;
import it.univr.track.repository.UnitLoadRepository; // legacy: in realtà UnitLoadRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final UnitLoadRepository unitLoadRepository; // legacy
    private final DeviceAssignmentService deviceAssignmentService;

    public PackageService(PackageRepository packageRepository,
                          UnitLoadRepository unitLoadRepository,
                          DeviceAssignmentService deviceAssignmentService) {
        this.packageRepository = packageRepository;
        this.unitLoadRepository = unitLoadRepository;
        this.deviceAssignmentService = deviceAssignmentService;
    }


    public Package createPackage() {
        Package pkg = new Package();
        return packageRepository.save(pkg);
    }

    @Transactional(readOnly = true)
    public Package getPackageById(Long packageId) {
        if (packageId == null) throw new IllegalArgumentException("packageId is null");
        return packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found: " + packageId));
    }

    @Transactional
    public boolean deletePackageById(Long packageId) {
        if (packageId == null) throw new IllegalArgumentException("packageId is null");
        if (packageRepository.existsById(packageId)) {
            packageRepository.deleteById(packageId);
            return true;
        }
        return false;
    }

    // UNITLOAD COMPOSITION

    @Transactional
    public void addUnitLoadToPackage(Long packageId, Long unitLoadId) {
        if (packageId == null) throw new IllegalArgumentException("packageId is null");
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");

        Package pkg = getPackageById(packageId);

        UnitLoad ul = unitLoadRepository.findById(unitLoadId)
                .orElseThrow(() -> new IllegalArgumentException("UnitLoad not found: " + unitLoadId));

        // Qui uso metodi di Dominio per mantenere coerenza (metodi protected chiamati dentro al public)
        pkg.addUnitLoad(ul);

        packageRepository.save(pkg);      // salva pkg + cascade sulle relazioni
        unitLoadRepository.save(ul);      // opzionale, ma safe se non sei sicuro del cascade
    }

    @Transactional
    public void removeUnitLoadFromPackage(Long packageId, Long unitLoadId) {
        Package pkg = getPackageById(packageId);

        UnitLoad ul = unitLoadRepository.findById(unitLoadId)
                .orElseThrow(() -> new IllegalArgumentException("UnitLoad not found: " + unitLoadId));

        // controllo base
        if (ul.getPkg() == null || !ul.getPkg().getId().equals(pkg.getId())) {
            throw new IllegalStateException("UnitLoad " + unitLoadId + " is not in Package " + packageId);
        }

        pkg.removeUnitLoad(ul);

        packageRepository.save(pkg);
        unitLoadRepository.save(ul);
    }

    @Transactional(readOnly = true)
    public Set<UnitLoad> getUnitLoadsOfPackage(Long packageId) {
        return getPackageById(packageId).getUnitLoads();
    }

    // DEVICE BINDING

    /**
     * Associa un device a un package (target = package).
     */
    @Transactional
    public void bindDeviceToPackage(Long packageId, Long deviceId) {
        if (packageId == null) throw new IllegalArgumentException("packageId is null");
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");

        // fail-fast package deve esistere
        getPackageById(packageId);

        deviceAssignmentService.bind(deviceId, packageId);
    }

    /**
     * Disassocia device (chiude assignment attivo).
     */
    @Transactional
    public void unbindDevice(Long deviceId) {
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");
        deviceAssignmentService.unbind(deviceId);
    }
}
