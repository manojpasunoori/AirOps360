package com.airops.wms.repository;

import com.airops.wms.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    List<Location> findByZone(String zone);

    @Query("SELECT l FROM Location l WHERE (l.capacity - l.occupied) >= :requiredCapacity ORDER BY (l.capacity - l.occupied) DESC")
    List<Location> findLocationsWithAvailableCapacity(int requiredCapacity);

    @Query("SELECT l FROM Location l WHERE l.occupied = 0")
    List<Location> findEmptyLocations();
}
