package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "trainees")
@PrimaryKeyJoinColumn(name = "user_id")
public class Trainee extends User {

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    private String address;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    @ManyToMany
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainers;

    public Trainee() {}

    public Trainee(String firstName, String lastName, Date dateOfBirth, String address, boolean isActive) {
        super(firstName, lastName, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Long getId() { return super.getId(); }

    public void setId(Long id) {
        super.setId(id);
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTrainers(List<Trainer> trainers) {
        this.trainers = trainers;
    }

    public List<Trainer> getTrainers() {
        return trainers;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }
}

