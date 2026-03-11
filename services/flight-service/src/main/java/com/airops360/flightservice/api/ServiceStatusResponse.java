package com.airops360.flightservice.api;

import java.time.OffsetDateTime;
import java.util.List;

public record ServiceStatusResponse(
    String service,
    String status,
    String version,
    OffsetDateTime timestamp,
    List<String> plannedCapabilities
) {
}
