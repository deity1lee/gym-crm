package org.example.entities;

public class TrainingType {
    private String name;

    public TrainingType(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return '\'' + name + '\'';
    }
}
