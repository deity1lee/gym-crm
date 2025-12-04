package org.example.entities;

public class Trainee extends User {
    private Long id;
    private String dateOfBirth;
    private String address;

    public Trainee(Long id, String firstName, String lastName, String dateOfBirth, String address) {
        super(firstName, lastName);
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Trainee(String firstName, String lastName, String dateOfBirth, String address) {
        super(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Long getId() { return id; }

    public String getDateOfBirth() { return dateOfBirth; }

    public void setId(Long id) {
        this.id = id;
    }
}

