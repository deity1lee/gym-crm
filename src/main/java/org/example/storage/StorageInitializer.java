package org.example.storage;

import org.example.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Component
public class StorageInitializer implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${trainee.data.file}")
    private String traineeFilePath;

    @Value("${trainer.data.file}")
    private String trainerFilePath;

    @Value("${training.data.file}")
    private String trainingFilePath;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof InMemoryStorage storage) {
            try {
                loadTrainees(storage);
                loadTrainers(storage);
                loadTrainings(storage);
            } catch (Exception e) {
                logger.error("Storage initialization failed: {}", e.getMessage());
            }
        }
        return bean;
    }
    private void loadTrainees(InMemoryStorage storage) {
        loadFile(traineeFilePath, line -> {
            String[] parts = line.split(",");
            Long id = Long.parseLong(parts[0]);
            Trainee trainee = new Trainee(id, parts[1], parts[2], parts[3], parts[4]);
            storage.getTraineeMap().put(id, trainee);
        });
        logger.info("Loaded trainees from {}", traineeFilePath);
    }
    private void loadTrainers(InMemoryStorage storage) {
        loadFile(trainerFilePath, line -> {
            String[] parts = line.split(",");
            Long id = Long.parseLong(parts[0]);
            Trainer trainer = new Trainer(id, parts[1], parts[2], parts[3]);
            storage.getTrainerMap().put(id, trainer);
        });
        logger.info("Loaded trainers from {}", trainerFilePath);
    }
    private void loadTrainings(InMemoryStorage storage) {
        loadFile(trainingFilePath, line -> {
            String[] parts = line.split(",");
            Long traineeId = Long.parseLong(parts[0]);
            Long trainerId = Long.parseLong(parts[1]);
            Long duration = Long.parseLong(parts[5]);
            TrainingType type = new TrainingType(parts[3]);
            Training training = new Training(
                    traineeId,
                    trainerId,
                    parts[2], type, parts[4], duration);
            storage.getTrainingMap().put(parts[2], training);
        });
        logger.info("Loaded trainings from {}", trainingFilePath);
    }

    private void loadFile(String path, Consumer<String> lineConsumer) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines()
                        .filter(line -> !line.isBlank() && !line.startsWith("#"))
                        .forEach(lineConsumer);
            }
        } catch (Exception e) {
            logger.warn("Could not load data from {}: {}", path, e.getMessage());
        }
    }
}
