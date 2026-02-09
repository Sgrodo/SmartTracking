package it.univr.track.repository;

import it.univr.track.entity.data.UnitLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UnitLoadRepository extends JpaRepository<UnitLoad, Long> {

    @Query("""
    select ul
    from UnitLoad ul
    where not exists (
        select 1
        from Shipment s
        join s.trackables sul
        where sul = ul
    )
""")
    List<UnitLoad> findAllNotInActiveShipments();

    @Query("""
    select ul
    from UnitLoad ul
    where not exists (
        select 1
        from Shipment s
        join s.trackables t
        where t = ul
    )
    and not exists (
        select 1
        from Package p
        join p.unitLoads pul
        where pul = ul
    )
""")
    List<UnitLoad> findAllNotInShipmentsAndNotInPackages();

}
