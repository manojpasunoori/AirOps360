package com.airops360.inventoryservice.api;

import com.airops360.inventoryservice.service.InventoryCacheService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryCacheController {

    private final InventoryCacheService inventoryCacheService;

    public InventoryCacheController(InventoryCacheService inventoryCacheService) {
        this.inventoryCacheService = inventoryCacheService;
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of(
            "service", "inventory-service",
            "status", "UP",
            "cache", "redis"
        );
    }

    @PostMapping("/cache")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public InventoryCacheRecord cache(@Valid @RequestBody InventoryCacheRequest request) {
        return inventoryCacheService.cache(
            inventoryCacheService.fromRequest(
                request.sku(),
                request.itemName(),
                request.storageZone(),
                request.quantityOnHand(),
                request.reorderThreshold()
            )
        );
    }

    @GetMapping("/cache/{sku}")
    public InventoryCacheRecord get(@PathVariable String sku) {
        return inventoryCacheService.get(sku)
            .orElseThrow(() -> new InventoryRecordNotFoundException(sku));
    }
}
