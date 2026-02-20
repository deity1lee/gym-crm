package org.workload.entities;

import java.util.ArrayList;
import java.util.List;

public class TrainerYear {

    private int year;
    private List<TrainerMonth> months = new ArrayList<>();

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
