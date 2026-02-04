package it.univr.track.controller.api;

import it.univr.track.controller.api.dto.PackageRequest;
import it.univr.track.controller.api.dto.UnitLoadRequest;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.LoadItemRepository;
import it.univr.track.repository.TrackableRepository;
import it.univr.track.service.PackageService;
import it.univr.track.service.TrackableService;
import it.univr.track.service.UnitLoadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Validated
@RestController()
@RequiredArgsConstructor
public class TrackableController {

    private final UnitLoadService unitLoadService;
    private final PackageService packageService;
    private final TrackableService trackableService;
    private final LoadItemRepository loadItemRepository;
    private final TrackableRepository trackableRepository;


    @PostMapping("/api/unitload")
    public boolean createUnitLoad(@Valid @RequestBody UnitLoadRequest request) {
        try {
            Set<LoadItem> loadItemSet = loadItemRepository.getLoadItemsByIdIn(request.getLoadItemIds());
            unitLoadService.createUnitLoad(
                    request.getDescription(),
                    request.getStartDate(),
                    request.getConstraints(),
                    loadItemSet
            );
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed");
        }
    }

    @PostMapping("/api/package")
    public Package createPackage(@Valid @RequestBody PackageRequest request) {
        try {
            Set<UnitLoad> unitLoads = trackableService.getUnitLoadsFromTrackableIds(request.getUnitLoadIds());

            return packageService.createPackage(unitLoads);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed");
        }
    }

    @DeleteMapping("/api/trackable/{trackableId}")
    public boolean deleteTrackable(@PathVariable Long trackableId) {
        if (!trackableRepository.existsById(trackableId)) {
            throw new NotFoundException("Trackable not found: " + trackableId);
        }
        trackableRepository.deleteById(trackableId);
        return true;
    }


}
