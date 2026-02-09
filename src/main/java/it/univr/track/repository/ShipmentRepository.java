package it.univr.track.repository;

import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.enumeration.ShipmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    // COMPLETED + CANCELLED
    @Query("""
        select s
        from Shipment s
        where s.state in (:finalStates)
    """)
    List<Shipment> findFinalShipments(
            @Param("finalStates") Collection<ShipmentState> finalStates
    );

    // TUTTI GLI ALTRI
    @Query("""
        select s
        from Shipment s
        where s.state not in (:finalStates)
    """)
    List<Shipment> findNonFinalShipments(
            @Param("finalStates") Collection<ShipmentState> finalStates
    );


}
