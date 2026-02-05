package it.univr.track.controller.api;

import it.univr.track.controller.api.dto.PackageRequest;
import it.univr.track.controller.api.dto.PackageResponse;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.data.Package;
import it.univr.track.service.PackageService;
import it.univr.track.service.TrackableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController implements CrudController<Package, PackageRequest, PackageResponse> {

    private final TrackableService trackableService;
    private final PackageService packageService;

    @Override
    @PostMapping({"/", ""})
    public PackageResponse create(@Valid @RequestBody PackageRequest request) {
        Set<UnitLoad> unitLoads = trackableService.getUnitLoadsFromTrackableIds(request.getUnitLoadIds());
        Package pkg = packageService.createPackage(request.getGroupCode(), request.getDescription(), unitLoads);
        return new PackageResponse(pkg);

    }

    @Override
    @GetMapping("/{trackableId}")
    public PackageResponse get(@PathVariable Long trackableId) {
        return new PackageResponse(packageService.getPackageById(trackableId));
    }

    @Override
    @GetMapping({"/", ""})
    public Iterable<PackageResponse> getAll() {
        return packageService.getAllPackages().stream()
                .map(PackageResponse::new)
                .toList();
    }

    @Override
    @DeleteMapping("/{trackableId}")
    public boolean deleteTrackable(@PathVariable Long trackableId) {
        return packageService.deletePackageById(trackableId);
    }
}
