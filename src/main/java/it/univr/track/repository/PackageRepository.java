package it.univr.track.repository;

import it.univr.track.entity.data.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Long> {
}
