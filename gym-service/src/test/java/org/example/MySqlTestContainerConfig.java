package org.example;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MySqlTestContainerConfig {

    @Container
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");

    static {
        mysql.start();
    }
}