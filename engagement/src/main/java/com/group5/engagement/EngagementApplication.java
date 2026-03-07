package com.group5.engagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EntityScan(basePackages = "com.group5.engagement.entity")
@EnableJpaRepositories(basePackages = "com.group5.engagement.repository")
@EnableDiscoveryClient
public class EngagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EngagementApplication.class, args);
	}

}
