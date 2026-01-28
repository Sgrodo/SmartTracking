package it.univr.track.repository;

import it.univr.track.entity.data.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {


}
