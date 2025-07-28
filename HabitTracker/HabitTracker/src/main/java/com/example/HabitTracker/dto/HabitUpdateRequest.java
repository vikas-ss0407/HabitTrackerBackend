package com.example.HabitTracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class HabitUpdateRequest {

    private String name;
    private String purpose;
    private Integer days;

    private List<Boolean> progress;

}
