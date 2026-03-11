package com.airops360.flightservice.integration.opensky;

import java.time.OffsetDateTime;

public record FlightTelemetry(
    String icao24,
    String callsign,
    String originCountry,
    OffsetDateTime telemetryTimestamp,
    Double longitude,
    Double latitude,
    Double barometricAltitude,
    Boolean onGround,
    Double velocity,
    Double heading,
    String source
) {
}
