package com.airops360.warehouseservice.api;

public record WarehouseReceiveResponse(
    String manifestReference,
    String sku,
    String status,
    String eventTopic
) {
}
