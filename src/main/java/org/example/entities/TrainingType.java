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
