package it.univr.track.repository;

import it.univr.track.entity.data.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {
    @Query("""
    select p
    from Package p
    where not exists (
        select 1
        from Shipment s
        join s.trackables t
        where t = p
    )
""")
    List<Package> findAllNotInAnyShipment();

    //Mi serve per il seleniumTest, non serve per la logica del sistema -> non lo metto nel Service
    Optional<Package> findByDescription(String description);
}
