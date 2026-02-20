package org.workload.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@CompoundIndex(name = "trainer_name_index", def = "{'firstName': 1, 'lastName': 1}")
@Document(collection = "trainer_workload")
public class TrainerWorkload {
    @Id
    private String id;
    @Indexed(unique = true)
    private String trainerUsername;
    private String firstName;
    private String lastName;
    private boolean active;
    private List<TrainerYear> years = new ArrayList<>();;

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<TrainerYear> getYears() {
        return years;
    }

    public void setYears(List<TrainerYear> years) {
        this.years = years;
    }
}