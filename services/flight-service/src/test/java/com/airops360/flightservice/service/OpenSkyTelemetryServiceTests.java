package com.airops360.flightservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.airops360.flightservice.config.OpenSkyProperties;
import com.airops360.flightservice.integration.opensky.OpenSkyClient;
import com.airops360.flightservice.integration.opensky.OpenSkyStatesResponse;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpenSkyTelemetryServiceTests {

    private final OpenSkyTelemetryService service = new OpenSkyTelemetryService(
        mock(OpenSkyClient.class),
        new OpenSkyProperties(
            "https://opensky-network.org",
            "/api/states/all",
            Duration.ofSeconds(10),
            "25.0",
            "49.5",
            "-124.8",
            "-66.9"
        )
    );

    @Test
    void normalizeMapsValidOpenSkyStateRows() {
        OpenSkyStatesResponse response = new OpenSkyStatesResponse(
            1710200000L,
            List.of(
                List.of(
                    "abc123",
                    "AA101   ",
                    "United States",
                    1710200000L,
                    1710200005L,
                    -97.0403,
                    32.8998,
                    3048.0,
                    false,
                    215.5,
                    182.1
                )
            )
        );

        var telemetry = service.normalize(response);

        assertThat(telemetry).hasSize(1);
        assertThat(telemetry.get(0).callsign()).isEqualTo("AA101");
        assertThat(telemetry.get(0).originCountry()).isEqualTo("United States");
        assertThat(telemetry.get(0).latitude()).isEqualTo(32.8998);
        assertThat(telemetry.get(0).source()).isEqualTo("OpenSky");
    }

    @Test
    void normalizeSkipsIncompleteRows() {
        OpenSkyStatesResponse response = new OpenSkyStatesResponse(
            1710200000L,
            List.of(
                List.of("short-row")
            )
        );

        assertThat(service.normalize(response)).isEmpty();
    }
}
