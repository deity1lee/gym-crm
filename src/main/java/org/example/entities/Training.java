package org.example.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "traineeId", nullable = false)
    private Trainee trainee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trainerId", nullable = false)
    private Trainer trainer;

    @Column(name = "trainingName", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "trainingTypeId", nullable = false)
    private TrainingType trainingType;

    @Temporal(TemporalType.DATE)
    @Column(name = "trainingDate", nullable = false)
    private Date date;

    @Column(name = "trainingDuration", nullable = false)
    private Long duration;

    public Training() {}

    public Training(Long id, Trainee trainee, Trainer trainer, String name, TrainingType type, Date date, Long duration) {
        this.id = id;
        this.trainee = trainee;
        this.trainer = trainer;
        this.name = name;
        this.trainingType = type;
        this.date = date;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
