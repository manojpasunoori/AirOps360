package com.airops360.flightservice.integration.opensky;

import com.airops360.flightservice.config.OpenSkyProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenSkyClient {

    private final RestClient restClient;
    private final OpenSkyProperties properties;

    public OpenSkyClient(RestClient openSkyRestClient, OpenSkyProperties properties) {
        this.restClient = openSkyRestClient;
        this.properties = properties;
    }

    public OpenSkyStatesResponse fetchStates(String lamin, String lomin, String lamax, String lomax) {
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(properties.statesPath())
                .queryParam("lamin", lamin)
                .queryParam("lomin", lomin)
                .queryParam("lamax", lamax)
                .queryParam("lomax", lomax)
                .build())
            .retrieve()
            .body(OpenSkyStatesResponse.class);
    }
}
