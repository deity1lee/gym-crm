package org.workload.config;

import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.springframework.test.context.DynamicPropertySource;

@Testcontainers
@Profile("integration-test")
public class MongoDBTestContainerConfig {
    @Container
    public static MongoDBContainer mongo = new MongoDBContainer("mongo:8.2.5");

    static {
        mongo.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> mongo.getReplicaSetUrl());
    }
}
