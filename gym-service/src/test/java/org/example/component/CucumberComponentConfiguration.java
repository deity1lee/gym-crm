package org.example.component;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.MySqlTestContainerConfig;
import org.example.service.TrainerWorkloadClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("component-test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@CucumberContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(MySqlTestContainerConfig.class)
public class CucumberComponentConfiguration {

    @MockitoBean
    private TrainerWorkloadClient trainerWorkloadClient;

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
