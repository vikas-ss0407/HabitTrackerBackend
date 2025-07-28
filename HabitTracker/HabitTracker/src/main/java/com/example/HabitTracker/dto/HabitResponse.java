package com.example.HabitTracker.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitResponse {

    private Long id;
    private String name;
    private String purpose;
    private List<Boolean> progress;
    private LocalDateTime createdDate;

}
