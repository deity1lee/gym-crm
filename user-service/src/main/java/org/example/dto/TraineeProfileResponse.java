package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.entities.TrainingType;

import java.time.LocalDate;
import java.util.List;

public class TraineeProfileResponse {

    private String username;
    private String firstName;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String address;
    private boolean active;

    private List<TrainerInfo> trainers;

    public static class TrainerInfo {
        private String username;
        private String firstName;
        private String lastName;
        private TrainingType trainingType;

        public TrainerInfo(String username, String firstName, String lastName, TrainingType trainingType) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.trainingType = trainingType;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public TrainingType getSpecialization() {
            return trainingType;
        }

        public void setSpecialization(TrainingType trainingType) {
            this.trainingType = trainingType;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<TrainerInfo> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerInfo> trainers) {
        this.trainers = trainers;
    }
}


