package com.example.HabitTracker.service;

import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import com.example.HabitTracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {
    private final HabitRepository habitRepo;

    public List<Habit> getHabits(User user) {
        return habitRepo.findByUser(user);
    }

    public Habit toggleHabit(Long habitId, int index) {
        Habit habit = habitRepo.findById(habitId).orElseThrow();
        List<Boolean> progress = habit.getProgress();
        progress.set(index, !progress.get(index));
        return habitRepo.save(habit);
    }
}
