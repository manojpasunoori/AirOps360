package com.airops360.flightservice.api;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightStatusController {

    private final String appVersion;

    public FlightStatusController(@Value("${app.version:dev}") String appVersion) {
        this.appVersion = appVersion;
    }

    @GetMapping("/status")
    public ServiceStatusResponse getStatus() {
        return new ServiceStatusResponse(
            "flight-service",
            "UP",
            appVersion,
            OffsetDateTime.now(),
            List.of(
                "ingest flight telemetry",
                "normalize arrival updates",
                "publish flight.arrival events",
                "persist flights to PostgreSQL"
            )
        );
    }
}
