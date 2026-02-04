package it.univr.track.service;

import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.TrackableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TrackableService {

    private final TrackableRepository trackableRepository;

    public TrackableService(TrackableRepository trackableRepository) {
        this.trackableRepository = trackableRepository;
    }

    @Transactional
    public Set<UnitLoad> getUnitLoadsFromTrackableIds(Set<Long> trackableIds) {
        List<Trackable> unitLoadList = trackableRepository.findAllById(trackableIds);
        Set<UnitLoad> result = new HashSet<>();

        for (Trackable t : unitLoadList) {
            result.addAll(t.getUnitLoads());
        }

        return result;
    }

    @Transactional
    public Set<Trackable> getAllTrackablesById(Iterable<Long> trackableIds) {
        return new HashSet<>(trackableRepository.findAllById(trackableIds));
    }

    @Transactional(readOnly = true)
    public Trackable getById(Long id) {
        return trackableRepository.findById(id).orElseThrow(() -> new NullPointerException("No trackable found with id " + id));
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (trackableRepository.existsById(id)) {
            throw new NotFoundException("Trackable " + id + " not found");
        }
        trackableRepository.deleteById(id);
        return true;
    }

}
