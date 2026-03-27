package org.example.integration;

import io.cucumber.java.After;
import io.cucumber.spring.CucumberContextConfiguration;
import org.example.MySqlTestContainerConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("integration-test")
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CucumberIntegrationConfiguration {

    private final MongoTemplate mongoTemplate;

    public CucumberIntegrationConfiguration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @After
    public void cleanMongo() {
        mongoTemplate.getDb().drop();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> MySqlTestContainerConfig.mysql.getJdbcUrl());
        registry.add("spring.datasource.username",
                () -> MySqlTestContainerConfig.mysql.getUsername());
        registry.add("spring.datasource.password",
                () -> MySqlTestContainerConfig.mysql.getPassword());
    }
}