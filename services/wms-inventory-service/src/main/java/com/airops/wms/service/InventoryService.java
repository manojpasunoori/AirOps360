package com.airops.wms.service;

import com.airops.wms.kafka.InventoryEventProducer;
import com.airops.wms.model.InventoryItem;
import com.airops.wms.model.Location;
import com.airops.wms.repository.InventoryItemRepository;
import com.airops.wms.repository.LocationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryItemRepository inventoryRepo;
    private final LocationRepository locationRepo;
    private final EntityManager entityManager;
    private final InventoryEventProducer eventProducer;

    // ── RECEIVE ──────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "inventory-summary", allEntries = true)
    public UUID receiveItems(String sku, String description, String category,
                              Integer quantity, UUID locationId,
                              String operatorId, String referenceNo) {

        log.info("Receiving {} units of SKU {} into location {}", quantity, sku, locationId);

        // Call stored procedure
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("wms.receive_items")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, UUID.class, ParameterMode.IN)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                .setParameter(1, sku)
                .setParameter(2, description)
                .setParameter(3, category)
                .setParameter(4, quantity)
                .setParameter(5, locationId)
                .setParameter(6, operatorId)
                .setParameter(7, referenceNo);

        query.execute();
        UUID itemId = (UUID) query.getSingleResult();

        // Publish Kafka event
        eventProducer.publishInventoryReceived(itemId, sku, quantity, locationId);

        log.info("Successfully received SKU {} — item ID: {}", sku, itemId);
        return itemId;
    }

    // ── PICK ─────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "inventory-summary", allEntries = true)
    public boolean pickItems(String sku, Integer quantity, String operatorId, String referenceNo) {

        log.info("Picking {} units of SKU {} for ref {}", quantity, sku, referenceNo);

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("wms.pick_items")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .setParameter(1, sku)
                .setParameter(2, quantity)
                .setParameter(3, operatorId)
                .setParameter(4, referenceNo);

        query.execute();
        Boolean result = (Boolean) query.getSingleResult();

        if (Boolean.TRUE.equals(result)) {
            eventProducer.publishItemsPicked(sku, quantity, referenceNo);
        }

        return Boolean.TRUE.equals(result);
    }

    // ── QUERY ────────────────────────────────────────────────────

    @Cacheable(value = "inventory-items", key = "#sku")
    public InventoryItem findBySku(String sku) {
        return inventoryRepo.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Item not found: " + sku));
    }

    @Cacheable(value = "inventory-summary")
    public List<Object[]> getInventorySummary() {
        return entityManager
                .createNativeQuery("SELECT * FROM wms.get_inventory_summary()")
                .getResultList();
    }

    public List<InventoryItem> findLowStockItems(int threshold) {
        return inventoryRepo.findByQuantityLessThanAndStatus(threshold, InventoryItem.ItemStatus.AVAILABLE);
    }

    public List<Location> findAvailableLocations(int requiredCapacity) {
        return locationRepo.findLocationsWithAvailableCapacity(requiredCapacity);
    }

    public List<InventoryItem> getAllItems() {
        return inventoryRepo.findAll();
    }
}
