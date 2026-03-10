package com.airops.wms.repository;

import com.airops.wms.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    Optional<InventoryItem> findBySku(String sku);

    List<InventoryItem> findByCategory(String category);

    List<InventoryItem> findByQuantityLessThanAndStatus(
            int threshold, InventoryItem.ItemStatus status);

    @Query("SELECT i FROM InventoryItem i WHERE i.location.zone = :zone AND i.status = 'AVAILABLE'")
    List<InventoryItem> findAvailableItemsByZone(String zone);

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.status = :status")
    long countByStatus(InventoryItem.ItemStatus status);
}
