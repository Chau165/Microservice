package com.group1.franchiseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FranchiseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FranchiseServiceApplication.class, args);
	}

}
