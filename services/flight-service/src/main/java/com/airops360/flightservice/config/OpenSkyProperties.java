package com.airops360.flightservice.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.opensky")
public record OpenSkyProperties(
    String baseUrl,
    String statesPath,
    Duration timeout,
    String defaultLamMin,
    String defaultLamMax,
    String defaultLomin,
    String defaultLomax
) {
}
