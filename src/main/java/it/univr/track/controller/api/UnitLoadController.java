package it.univr.track.controller.api;

import it.univr.track.controller.api.dto.UnitLoadRequest;
import it.univr.track.controller.api.dto.UnitLoadResponse;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.LoadItemRepository;
import it.univr.track.service.UnitLoadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Stream;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unitloads")
public class UnitLoadController implements CrudController<UnitLoad, UnitLoadRequest, UnitLoadResponse> {

    private final UnitLoadService unitLoadService;
    private final LoadItemRepository loadItemRepository;

    @Override
    @PostMapping({"/", ""})
    public UnitLoadResponse create(@Valid @RequestBody UnitLoadRequest request) {
        Set<LoadItem> loadItemSet = loadItemRepository.getLoadItemsByIdIn(request.getLoadItemIds());
        if (loadItemSet.size() < request.getLoadItemIds().size()) {
            throw new NotFoundException("One or more LoadItems not found");
        }

        UnitLoad ut = unitLoadService.createUnitLoad(
                request.getDescription(),
                request.getStartDate(),
                request.getConstraints(),
                loadItemSet
        );

        return new UnitLoadResponse(ut);
    }

    @Override
    @GetMapping("/{trackableId}")
    public UnitLoadResponse get(@PathVariable Long trackableId) {
        UnitLoad ut = unitLoadService.getUnitLoadById(trackableId);
        return new UnitLoadResponse(ut);
    }

    @Override
    @GetMapping({"/", ""})
    public Iterable<UnitLoadResponse> getAll() {
        return Stream.of(unitLoadService.getAllUnitLoads())
                .map(UnitLoadResponse::new)
                .toList();
    }

    @Override
    @PutMapping("/{trackableId}")
    public UnitLoadResponse update(@PathVariable Long trackableId, @Valid @RequestBody UnitLoadRequest request) {
        // TODO implement update logic
        return null;
    }

    @Override
    @DeleteMapping("/{trackableId}")
    public boolean deleteTrackable(@PathVariable Long trackableId) {
        return unitLoadService.deleteUnitLoadById(trackableId);
    }
}
