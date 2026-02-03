package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.enumeration.ShipmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("""
        select distinct da.device
        from Shipment s
        join s.trackables trk
        join DeviceAssignment da on da.target = trk
        where s.state not in :states
          and da.toTs is null
    """)
    List<Device> findDevicesByNotInShipmentStates(
            @Param("states") Collection<ShipmentState> states
    );
}
