package com.airops360.flightservice.api;

import com.airops360.flightservice.integration.opensky.FlightTelemetry;
import com.airops360.flightservice.service.OpenSkyTelemetryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightTelemetryController {

    private final OpenSkyTelemetryService telemetryService;

    public FlightTelemetryController(OpenSkyTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping("/telemetry")
    public List<FlightTelemetry> getTelemetry(
        @RequestParam(required = false) String lamin,
        @RequestParam(required = false) String lomin,
        @RequestParam(required = false) String lamax,
        @RequestParam(required = false) String lomax
    ) {
        return telemetryService.fetchTelemetry(lamin, lomin, lamax, lomax);
    }
}
