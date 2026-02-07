package it.univr.track.service;

import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final UnitLoadService unitLoadService;

    @Transactional(readOnly = true)
    public Package getPackageById(Long packageId) {
        if (packageId == null) throw new IllegalArgumentException("packageId is null");
        return packageRepository.findById(packageId)
                .orElseThrow(() -> new NotFoundException("Package not found: " + packageId));
    }

    @Transactional
    public boolean deletePackageById(Long packageId) {
        if (packageId == null) throw new InvalidDataException("packageId is null");
        if (!packageRepository.existsById(packageId)) {
            throw new NotFoundException("Package not found: " + packageId);
        }
        packageRepository.deleteById(packageId);
        return true;
    }

    // UNITLOAD COMPOSITION

    @Transactional
    public void addUnitLoadToPackage(Long packageId, Long unitLoadId) {
        if (packageId == null) throw new InvalidDataException("packageId is null");
        if (unitLoadId == null) throw new InvalidDataException("unitLoadId is null");

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
            throw new NotFoundException("UnitLoad " + unitLoadId + " is not in Package " + packageId);
        }

        pkg.removeUnitLoad(ul);
    }

    @Transactional(readOnly = true)
    public Set<UnitLoad> getUnitLoadsOfPackage(Long packageId) {
        return getPackageById(packageId).getUnitLoads();
    }

    @Transactional
    public Package createPackage(String groupCode, String description, Set<UnitLoad> unitLoads) {
        if (unitLoads == null || unitLoads.isEmpty()){
            throw new InvalidDataException("unitLoads is null or empty");
        }
        Package pkg = new Package(description, groupCode);
        pkg = packageRepository.save(pkg); // così ha l'id

        for (UnitLoad ul : unitLoads) {
            pkg.addUnitLoad(ul);
        }
        return packageRepository.save(pkg);
    }

    @Transactional(readOnly = true)
    public Set<Package> getAllPackages() {
        return Set.copyOf(packageRepository.findAll());
    }

    @Transactional
    public List<Package> getAllPackagesNotInShipments(){
        return packageRepository.findAllNotInAnyShipment();
    }
}
