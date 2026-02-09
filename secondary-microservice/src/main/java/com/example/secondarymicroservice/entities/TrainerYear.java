package com.example.secondarymicroservice.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TrainerYear {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "calendar_year")
    private int year;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerMonth> months = new ArrayList<>();;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<TrainerMonth> getMonths() {
        return months;
    }

    public void setMonths(List<TrainerMonth> months) {
        this.months = months;
    }
}
