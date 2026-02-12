package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.Trackable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceAssignmentRepository extends JpaRepository<DeviceAssignment, Long> {

    Optional<DeviceAssignment> findFirstByDeviceAndToTsIsNull(Device device);

    List<DeviceAssignment> findByTarget(Trackable target);

    Optional<DeviceAssignment> findFirstByTargetAndToTsIsNull(Trackable target);

    @Query("""
                select a from DeviceAssignment a
                where a.device = :device
                  and a.fromTs <= :ts
                  and (a.toTs is null or a.toTs > :ts)
                order by a.fromTs desc
            """)
    Optional<DeviceAssignment> findActiveAt(@Param("device") Device device,
                                            @Param("ts") ZonedDateTime ts);

    List<DeviceAssignment> findByTargetAndToTsIsNull(Trackable target);

    @Query("""
    select da
    from DeviceAssignment da
    join fetch da.device d
    where da.toTs is null
      and da.target.id in :ids
""")
    List<DeviceAssignment> findActiveByTargetIds(@Param("ids") List<Long> ids);

    Optional<DeviceAssignment> findFirstByTargetAndDeviceAndToTsIsNull(
            Trackable target,
            Device device
    );

}
