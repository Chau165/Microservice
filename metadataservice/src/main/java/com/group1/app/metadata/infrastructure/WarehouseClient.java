package com.group1.app.metadata.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WarehouseClient {

    private final WebClient warehouseWebClient;

    public JsonNode getWarehouseById(String warehouseId) {
        try {
            return warehouseWebClient.get()
                    .uri("/api/warehouse-service/warehouses/{id}", warehouseId)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}
