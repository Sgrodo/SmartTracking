package it.univr.track.controller.api;

import it.univr.track.entity.data.Trackable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface CrudController<T, P, R> {

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public R create(@Valid @RequestBody P request);

    @GetMapping("/{trackableId}")
    @ResponseStatus(HttpStatus.OK)
    public R get(@PathVariable Long trackableId);

    @GetMapping("/")
    @ResponseStatus(HttpStatus.FOUND)
    public Iterable<R> getAll();

    @DeleteMapping("/{trackableId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteTrackable(@PathVariable Long trackableId);


}
