package com.airops.wms.controller;

import com.airops.wms.model.InventoryItem;
import com.airops.wms.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WMS Inventory", description = "Warehouse Management — Inventory Operations")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/receive")
    @Operation(summary = "Receive items into warehouse")
    public ResponseEntity<Map<String, Object>> receiveItems(@Valid @RequestBody ReceiveRequest req) {
        UUID itemId = inventoryService.receiveItems(
                req.getSku(), req.getDescription(), req.getCategory(),
                req.getQuantity(), req.getLocationId(),
                req.getOperatorId(), req.getReferenceNo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "itemId", itemId,
                "message", "Items received successfully",
                "sku", req.getSku(),
                "quantity", req.getQuantity()
        ));
    }

    @PostMapping("/pick")
    @Operation(summary = "Pick items for shipment")
    public ResponseEntity<Map<String, Object>> pickItems(@Valid @RequestBody PickRequest req) {
        boolean success = inventoryService.pickItems(
                req.getSku(), req.getQuantity(), req.getOperatorId(), req.getReferenceNo()
        );
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "Items picked successfully" : "Pick failed",
                "sku", req.getSku(),
                "quantity", req.getQuantity()
        ));
    }

    @GetMapping("/item/{sku}")
    @Operation(summary = "Get inventory item by SKU")
    public ResponseEntity<InventoryItem> getItemBySku(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.findBySku(sku));
    }

    @GetMapping("/items")
    @Operation(summary = "Get all inventory items")
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/summary")
    @Operation(summary = "Get inventory summary by zone")
    public ResponseEntity<List<Object[]>> getInventorySummary() {
        return ResponseEntity.ok(inventoryService.getInventorySummary());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get items below stock threshold")
    public ResponseEntity<List<InventoryItem>> getLowStockItems(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(inventoryService.findLowStockItems(threshold));
    }

    @GetMapping("/locations/available")
    @Operation(summary = "Find locations with available capacity")
    public ResponseEntity<?> getAvailableLocations(
            @RequestParam(defaultValue = "1") int requiredCapacity) {
        return ResponseEntity.ok(inventoryService.findAvailableLocations(requiredCapacity));
    }

    // ── Request DTOs ─────────────────────────────────────────────

    @Data
    public static class ReceiveRequest {
        @NotBlank private String sku;
        @NotBlank private String description;
        @NotBlank private String category;
        @NotNull @Positive private Integer quantity;
        @NotNull private UUID locationId;
        @NotBlank private String operatorId;
        @NotBlank private String referenceNo;
        private BigDecimal unitWeightKg;
    }

    @Data
    public static class PickRequest {
        @NotBlank private String sku;
        @NotNull @Positive private Integer quantity;
        @NotBlank private String operatorId;
        @NotBlank private String referenceNo;
    }
}
