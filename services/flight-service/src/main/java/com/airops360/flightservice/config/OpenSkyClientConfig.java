package com.airops360.flightservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenSkyClientConfig {

    @Bean
    public RestClient openSkyRestClient(OpenSkyProperties properties, RestClient.Builder builder) {
        return builder
            .baseUrl(properties.baseUrl())
            .build();
    }
}
