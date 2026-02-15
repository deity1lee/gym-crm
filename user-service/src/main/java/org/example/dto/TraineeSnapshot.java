package org.example.dto;

import org.example.entities.Trainee;
import org.example.entities.Trainer;

import java.time.LocalDate;
import java.util.List;

public class TraineeSnapshot {
    public String username;
    public String firstName;
    public String lastName;
    public LocalDate dateOfBirth;
    public String address;
    public boolean isActive;

    public List<Long> trainerIds;
    public List<TrainingSnapshot> trainings;
    public String password;

    public static TraineeSnapshot from(Trainee trainee) {
        TraineeSnapshot s = new TraineeSnapshot();

        // ---- User fields (parent class)
        s.username = trainee.getUsername();
        s.firstName = trainee.getFirstName();
        s.lastName = trainee.getLastName();
        s.password = trainee.getPassword();
        s.isActive = trainee.isActive();

        // ---- Trainee fields
        s.dateOfBirth = trainee.getDateOfBirth();
        s.address = trainee.getAddress();

        // ---- ManyToMany: trainers → IDs only
        s.trainerIds = trainee.getTrainers()
                .stream()
                .map(Trainer::getId)
                .toList();

        // ---- OneToMany: trainings → snapshots
        s.trainings = trainee.getTrainings()
                .stream()
                .map(TrainingSnapshot::from)
                .toList();

        return s;
    }
}
