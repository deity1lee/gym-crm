package org.example.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "trainingTypes")
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trainingTypeName;

    @OneToMany(mappedBy = "trainingType")
    private List<Trainer> trainers;

    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainings;

    public TrainingType() {}

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }
}
