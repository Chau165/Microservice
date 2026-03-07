package com.group5.engagement.client;

import com.group5.engagement.client.dto.ExternalOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${application.config.order-service-url}")
public interface OrderClient {

    @GetMapping("/api/v1/orders/{id}")
    ExternalOrderResponse getOrderById(@PathVariable("id") String id);
}