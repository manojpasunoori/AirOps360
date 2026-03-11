package com.airops360.flightservice.integration.opensky;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenSkyStatesResponse(
    Long time,
    List<List<Object>> states
) {
}
