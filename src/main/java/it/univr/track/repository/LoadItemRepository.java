package it.univr.track.repository;

import it.univr.track.entity.data.LoadItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadItemRepository extends JpaRepository<LoadItem, Long> {
}
