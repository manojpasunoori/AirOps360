package com.airops360.inventoryservice.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryCacheRequest(
    @NotBlank String sku,
    @NotBlank String itemName,
    @NotBlank String storageZone,
    @Min(0) int quantityOnHand,
    @Min(0) int reorderThreshold
) {
}
