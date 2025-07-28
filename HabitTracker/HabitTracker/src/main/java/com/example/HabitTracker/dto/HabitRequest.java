package com.example.HabitTracker.dto;

import lombok.Data;

@Data
public class HabitRequest {

    private String name;
    private String purpose;
    private Integer days;

}
