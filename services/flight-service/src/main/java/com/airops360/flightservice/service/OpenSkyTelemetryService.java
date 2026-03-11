package com.airops360.flightservice.service;

import com.airops360.flightservice.config.OpenSkyProperties;
import com.airops360.flightservice.integration.opensky.FlightTelemetry;
import com.airops360.flightservice.integration.opensky.OpenSkyClient;
import com.airops360.flightservice.integration.opensky.OpenSkyStatesResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OpenSkyTelemetryService {

    private final OpenSkyClient openSkyClient;
    private final OpenSkyProperties properties;

    public OpenSkyTelemetryService(OpenSkyClient openSkyClient, OpenSkyProperties properties) {
        this.openSkyClient = openSkyClient;
        this.properties = properties;
    }

    public List<FlightTelemetry> fetchTelemetry(String lamin, String lomin, String lamax, String lomax) {
        String resolvedLamin = valueOrDefault(lamin, properties.defaultLamMin());
        String resolvedLomin = valueOrDefault(lomin, properties.defaultLomin());
        String resolvedLamax = valueOrDefault(lamax, properties.defaultLamMax());
        String resolvedLomax = valueOrDefault(lomax, properties.defaultLomax());

        OpenSkyStatesResponse response = openSkyClient.fetchStates(resolvedLamin, resolvedLomin, resolvedLamax, resolvedLomax);
        return normalize(response);
    }

    List<FlightTelemetry> normalize(OpenSkyStatesResponse response) {
        if (response == null || response.states() == null) {
            return Collections.emptyList();
        }

        return response.states().stream()
            .map(this::toTelemetry)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private FlightTelemetry toTelemetry(List<Object> state) {
        if (state == null || state.size() < 11) {
            return null;
        }

        String icao24 = trimToNull(asString(state.get(0)));
        if (icao24 == null) {
            return null;
        }

        try {
            String callsign = trimToNull(asString(state.get(1)));
            String originCountry = trimToNull(asString(state.get(2)));
            OffsetDateTime timestamp = asEpochSeconds(state.get(3))
                .or(() -> asEpochSeconds(state.get(4)))
                .map(value -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneOffset.UTC))
                .orElse(null);

            return new FlightTelemetry(
                icao24,
                callsign,
                originCountry,
                timestamp,
                asDouble(state.get(5)),
                asDouble(state.get(6)),
                asDouble(state.get(7)),
                asBoolean(state.get(8)),
                asDouble(state.get(9)),
                asDouble(state.get(10)),
                "OpenSky"
            );
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String valueOrDefault(String supplied, String fallback) {
        return supplied == null || supplied.isBlank() ? fallback : supplied;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Double.parseDouble(text);
        }
        return null;
    }

    private Boolean asBoolean(Object value) {
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof String text && !text.isBlank()) {
            return Boolean.parseBoolean(text);
        }
        return null;
    }

    private Optional<Long> asEpochSeconds(Object value) {
        if (value instanceof Number number) {
            return Optional.of(number.longValue());
        }
        if (value instanceof String text && !text.isBlank()) {
            return Optional.of(Long.parseLong(text));
        }
        return Optional.empty();
    }
}
