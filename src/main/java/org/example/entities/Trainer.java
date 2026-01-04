package org.example.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Trainer extends User {

    @OneToMany(mappedBy = "trainer")
    private List<Training> trainings;

    @ManyToOne(optional = false)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @ManyToMany(mappedBy = "trainers")
    private List<Trainee> trainees;

    public Trainer() {}

    public Trainer(String firstName, String lastName,
                   TrainingType trainingType) {
        super(firstName, lastName, true);
        this.trainingType = trainingType;
    }

    public Long getId() {
        return super.getId();
    }

    public void setId(Long id) {
        super.setId(id);
    }
}

