package com.example.HabitTracker.dto;

import lombok.Data;

@Data
public class HabitRequest {

    private String name;
    private String purpose;
    private Integer days;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
}
