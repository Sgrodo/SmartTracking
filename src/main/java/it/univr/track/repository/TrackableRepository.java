package it.univr.track.repository;

import it.univr.track.entity.data.Trackable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackableRepository extends JpaRepository<Trackable, Long> {}
