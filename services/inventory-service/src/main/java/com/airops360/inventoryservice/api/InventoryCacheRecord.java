package com.airops360.inventoryservice.api;

import java.time.OffsetDateTime;

public record InventoryCacheRecord(
    String sku,
    String itemName,
    String storageZone,
    int quantityOnHand,
    int reorderThreshold,
    OffsetDateTime cachedAt
) {
}
