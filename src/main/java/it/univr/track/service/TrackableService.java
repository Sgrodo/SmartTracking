package it.univr.track.service;

import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.repository.TrackableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrackableService {

    private TrackableRepository trackableRepository;

    public TrackableService(TrackableRepository trackableRepository){
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
}
