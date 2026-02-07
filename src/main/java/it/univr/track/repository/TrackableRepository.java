package it.univr.track.repository;

import it.univr.track.entity.data.Trackable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrackableRepository extends JpaRepository<Trackable, Long> {
    @Query("""
        select t
        from Trackable t
        where not exists (
            select 1
            from Shipment s
            join s.trackables st
            where st = t
        )
    """)
    List<Trackable> findAllNotInActiveShipments();

}
