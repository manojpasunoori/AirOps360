package com.airops360.warehouseservice.api;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    private final String appVersion;

    public WarehouseController(@Value("${app.version:dev}") String appVersion) {
        this.appVersion = appVersion;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
            "service", "warehouse-service",
            "status", "UP",
            "version", appVersion,
            "timestamp", OffsetDateTime.now(),
            "capabilities", new String[] {"receive cargo", "register warehouse intake", "publish warehouse.receive events"}
        );
    }

    @PostMapping("/receive")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WarehouseReceiveResponse receive(@Valid @RequestBody WarehouseReceiveRequest request) {
        return new WarehouseReceiveResponse(
            request.manifestReference(),
            request.sku(),
            "RECEIVED",
            "warehouse.receive"
        );
    }
}
