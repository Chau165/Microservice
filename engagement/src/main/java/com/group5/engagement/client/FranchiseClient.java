package com.group5.engagement.client;

import com.group5.engagement.client.dto.ExternalFranchiseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "franchise-service", url = "${application.config.franchise-service-url}")
public interface FranchiseClient {

    @GetMapping("/api/v1/franchises/{id}")
    ExternalFranchiseResponse getFranchiseById(@PathVariable("id") Long id);
}