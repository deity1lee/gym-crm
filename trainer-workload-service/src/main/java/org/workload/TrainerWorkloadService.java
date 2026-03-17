package org.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainerWorkloadService {
	public static void main(String[] args) {
		SpringApplication.run(TrainerWorkloadService.class, args);
	}
}
