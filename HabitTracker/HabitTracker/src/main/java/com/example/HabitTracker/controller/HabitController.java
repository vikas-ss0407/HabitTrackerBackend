package com.example.HabitTracker.controller;

import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import com.example.HabitTracker.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {
    private final HabitService habitService;

    @GetMapping
    public List<Habit> getHabits(@AuthenticationPrincipal User user) {
        return habitService.getHabits(user);
    }

    @PatchMapping("/{habitId}/toggle/{index}")
    public Habit toggleProgress(@PathVariable Long habitId, @PathVariable int index) {
        return habitService.toggleHabit(habitId, index);
    }
}
