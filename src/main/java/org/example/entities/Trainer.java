package org.example.entities;

public class Trainer extends User {
    private Long id;
    private String specialization;

    public Trainer(Long id, String firstName, String lastName, String specialization) {
        super(firstName, lastName);
        this.id = id;
        this.specialization = specialization;
    }
    public Long getId() {
        return id;
    }
}

