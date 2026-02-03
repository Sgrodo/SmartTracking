package it.univr.track.service;

import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.repository.PackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final UnitLoadService unitLoadService;

    public PackageService(PackageRepository packageRepository,
                          UnitLoadService unitLoadService) {
        this.packageRepository = packageRepository;
        this.unitLoadService = unitLoadService;
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

        UnitLoad ul = unitLoadService.getUnitLoadById(unitLoadId);

        // Qui uso metodi di Dominio per mantenere coerenza (metodi protected chiamati dentro al public)
        pkg.addUnitLoad(ul);
    }

    @Transactional
    public void removeUnitLoadFromPackage(Long packageId, Long unitLoadId) {
        Package pkg = getPackageById(packageId);

        UnitLoad ul = unitLoadService.getUnitLoadById(unitLoadId);

        // controllo base
        if (ul.getPkg() == null || !ul.getPkg().getId().equals(pkg.getId())) {
            throw new IllegalStateException("UnitLoad " + unitLoadId + " is not in Package " + packageId);
        }

        pkg.removeUnitLoad(ul);
    }

    @Transactional(readOnly = true)
    public Set<UnitLoad> getUnitLoadsOfPackage(Long packageId) {
        return getPackageById(packageId).getUnitLoads();
    }

    @Transactional
    public Package createPackage(Set<UnitLoad> unitLoads) {
        Package pkg = new Package(unitLoads);
        return packageRepository.save(pkg); // così ha l'id
    }

}
