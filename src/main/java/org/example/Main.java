package org.example;

import org.example.config.AppConfig;
import org.example.entities.Trainee;
import org.example.service.TraineeService;
import org.example.utilities.TrainingSearchCriteria;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            LOGGER.info("Application context initialized successfully.");
        } catch (Exception e) {
            LOGGER.error("Application failed to start: {}", e.getMessage(), e);
            throw e;
        }
    }
}
