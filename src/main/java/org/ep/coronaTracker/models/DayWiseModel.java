package org.ep.coronaTracker.models;

import org.springframework.stereotype.Component;

@Component
public class DayWiseModel {
    private String day;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
