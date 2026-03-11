package com.airops360.inventoryservice.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(InventoryRecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(InventoryRecordNotFoundException exception) {
        return Map.of(
            "error", "inventory_record_not_found",
            "message", exception.getMessage()
        );
    }
}
