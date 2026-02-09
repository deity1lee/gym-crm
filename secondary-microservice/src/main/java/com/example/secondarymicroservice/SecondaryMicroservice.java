package com.example.secondarymicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SecondaryMicroservice {
	public static void main(String[] args) {
		SpringApplication.run(SecondaryMicroservice.class, args);
	}

}
