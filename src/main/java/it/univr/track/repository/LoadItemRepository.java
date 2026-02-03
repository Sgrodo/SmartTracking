package it.univr.track.repository;

import it.univr.track.entity.data.LoadItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface LoadItemRepository extends JpaRepository<LoadItem, Long> {

    Set<LoadItem> getLoadItemsByIdIn(Collection<Long> ids);

}
