package it.univr.track.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface CrdController<T, P, R> {

    @PostMapping({"/", ""})
    @ResponseStatus(HttpStatus.CREATED)
    R create(@Valid @RequestBody P request);

    @GetMapping("/{trackableId}")
    @ResponseStatus(HttpStatus.OK)
    R get(@PathVariable Long trackableId);

    @GetMapping({"/", ""})
    @ResponseStatus(HttpStatus.OK)
    Iterable<R> getAll();

    @DeleteMapping("/{trackableId}")
    @ResponseStatus(HttpStatus.OK)
    boolean deleteTrackable(@PathVariable Long trackableId);


}
