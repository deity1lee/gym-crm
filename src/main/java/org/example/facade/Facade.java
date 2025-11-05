package org.example.facade;

import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.stereotype.Component;

@Component
public class Facade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public Facade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public void showAllTrainees() {
        traineeService.getAllTrainees().forEach(System.out::println);
    }
    public void showAllTrainers() {
        trainerService.getAllTrainers().forEach(System.out::println);
    }
    public void showAllTrainings() {
        trainingService.getAllTrainings().forEach(System.out::println);
    }
}
