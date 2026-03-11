package com.airops360.inventoryservice.api;

public class InventoryRecordNotFoundException extends RuntimeException {

    public InventoryRecordNotFoundException(String sku) {
        super("No cached inventory record found for SKU " + sku);
    }
}
