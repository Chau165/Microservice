package service.CSFC.CSFC_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CsfcAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsfcAuthServiceApplication.class, args);
	}

}
