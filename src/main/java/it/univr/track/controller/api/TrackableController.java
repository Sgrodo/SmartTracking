package it.univr.track.controller.api;

import it.univr.track.controller.api.dto.PackageRequest;
import it.univr.track.controller.api.dto.UnitLoadRequest;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.repository.LoadItemRepository;
import it.univr.track.repository.UnitLoadRepository;
import it.univr.track.service.PackageService;
import it.univr.track.service.UnitLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController()
@RequestMapping({"/api/unitload","/api/package"})
public class TrackableController {

    private final UnitLoadService unitLoadService;
    private final PackageService packageService;
    private final UnitLoadRepository unitLoadRepository;
    private final LoadItemRepository loadItemRepository;
    public TrackableController(UnitLoadService unitLoadService, PackageService packageService, UnitLoadRepository unitLoadRepository, LoadItemRepository loadItemRepository){
        this.unitLoadService = unitLoadService;
        this.packageService = packageService;
        this.unitLoadRepository = unitLoadRepository;
        this.loadItemRepository = loadItemRepository;
    }
    @PostMapping("/api/unitload")
    public boolean createUnitLoad(@RequestParam UnitLoadRequest request) {
        try {
            List<LoadItem> list = new ArrayList<>();
            for (long id : request.getLoadItems()) {
                if(loadItemRepository.findById(id).isEmpty()){
                    throw new NullPointerException("LoadItem of id: "+ id + "does not exist");
                }
                LoadItem referenceById = loadItemRepository.getReferenceById(id);
                list.add(referenceById);
            }
            unitLoadService.createUnitLoad(
                        request.getDescription(),
                        request.getStartDate(),
                        request.getConstraints(),
                        list
                        );
            return true;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed");
        }
    }

    @PostMapping("/api/package")
    public Package createPackage(@RequestBody PackageRequest request) {
        try {
            Set<Long> unitLoadIds = (request != null) ? request.getUnitLoadIds() : null;
            Set<UnitLoad> loadItems = new HashSet<UnitLoad>();

            if(unitLoadIds == null){
                throw new NullPointerException("UnitLoadsIds is null");
            }
            for( Long id : unitLoadIds){
                Optional<UnitLoad> byId = unitLoadRepository.findById(id);
                byId.ifPresent(loadItems::add);
            }
            return packageService.createPackage(loadItems);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed");
        }
    }
    @DeleteMapping("/api/package/{packageId}")
    public boolean deletePackage(@PathVariable Long packageId){
        return packageService.deletePackageById(packageId);
    }

    @DeleteMapping("/api/package/{packageId}")
    public boolean deleteUnitLoad(@PathVariable Long unitLoadId){
        return unitLoadService.deleteUnitLoadById(unitLoadId);
    }

}
