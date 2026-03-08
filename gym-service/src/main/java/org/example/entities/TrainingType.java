package org.example.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "training_types")
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_type_name", nullable = false)
    private String trainingTypeName;

    public TrainingType() {}

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    public TrainingType(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }

    public Long getId() {
        return id;
    }
    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
