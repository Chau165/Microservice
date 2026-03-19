package com.group1.app.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://microservice-1-7foh.onrender.com")
                .build();
    }

    @Bean
    public WebClient warehouseWebClient(
            @org.springframework.beans.factory.annotation.Value("${internal.services.warehouse-service:http://localhost:8085}") String warehouseServiceUrl
    ) {
        return WebClient.builder()
                .baseUrl(warehouseServiceUrl)
                .build();
    }

}