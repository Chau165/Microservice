package com.group5.engagement.client;

import com.group5.engagement.client.dto.ExternalUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name: tên service trên Eureka, url: dùng khi test local hoặc không có Eureka
@FeignClient(name = "user-service", url = "${application.config.user-service-url}")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    ExternalUserResponse getUserById(@PathVariable("id") Long id);
}