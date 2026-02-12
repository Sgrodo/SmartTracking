package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.enumeration.ShipmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("""
select d
from Device d
where not exists (
  select 1
  from DeviceAssignment da
  join da.target t
  join Shipment s
  join s.trackables st
  where da.device = d
    and da.toTs is null
    and st = t
    and s.state in :states
)
""")
    List<Device> findDevicesByNotInShipmentStates(
            @Param("states") Collection<ShipmentState> states
    );


    // Nessun assignment attivo
    @Query("""
select d
from Device d
where not exists (
  select 1 from DeviceAssignment da
  where da.device = d and da.toTs is null
)
""")
    List<Device> findDevicesWithNoActiveAssignment();


    // NON busy: non esiste assignment attivo che lo collega a shipment in stati busy
    @Query("""
select d
from Device d
where not exists (
  select 1
  from DeviceAssignment da
  join da.target t
  join Shipment s
    on t member of s.trackables
  where da.device = d
    and da.toTs is null
    and s.state in :busyStates
)
""")
    List<Device> findDevicesNotInBusyShipments(@Param("busyStates") Collection<ShipmentState> busyStates);

    //per selenium Test
    Optional<Device> findByOrigin(String origin);
}
