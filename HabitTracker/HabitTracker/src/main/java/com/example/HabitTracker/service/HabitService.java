package com.example.HabitTracker.service;

import com.example.HabitTracker.dto.HabitRequest;
import com.example.HabitTracker.dto.HabitUpdateRequest;
import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import com.example.HabitTracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public Habit createHabit(Habit habit, User user) {
        habit.setUser(user);
        // Use the progress array sent from frontend, or create default 21-day habit
        if (habit.getProgress() == null || habit.getProgress().isEmpty()) {
            List<Boolean> progress = new ArrayList<>();
            for (int i = 0; i < 21; i++) {
                progress.add(false);
            }
            habit.setProgress(progress);
        }
        return habitRepo.save(habit);
    }

    @Transactional
    public Habit createHabitFromRequest(HabitRequest request, User user) {
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setPurpose(request.getPurpose());
        habit.setUser(user);

        // Create progress array based on requested days
        List<Boolean> progress = new ArrayList<>();
        for (int i = 0; i < request.getDays(); i++) {
            progress.add(false);
        }
        habit.setProgress(progress);

        return habitRepo.save(habit);
    }

    @Transactional
    public Habit updateHabit(Long habitId, Habit habitRequest, User user) {
        Habit habit = habitRepo.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!habit.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this habit");
        }

        // Update habit fields
        habit.setName(habitRequest.getName());
        habit.setPurpose(habitRequest.getPurpose());

        // If progress array is provided, update it (but preserve existing progress)
        if (habitRequest.getProgress() != null && !habitRequest.getProgress().isEmpty()) {
            // Only update if the new progress array has a different size
            if (habitRequest.getProgress().size() != habit.getProgress().size()) {
                List<Boolean> newProgress = new ArrayList<>();
                int minSize = Math.min(habit.getProgress().size(), habitRequest.getProgress().size());

                // Copy existing progress for the overlapping days
                for (int i = 0; i < minSize; i++) {
                    newProgress.add(habit.getProgress().get(i));
                }

                // Add false for any additional days
                for (int i = minSize; i < habitRequest.getProgress().size(); i++) {
                    newProgress.add(false);
                }

                habit.setProgress(newProgress);
            }
        }

        return habitRepo.save(habit);
    }

    @Transactional
    public void deleteHabit(Long habitId, User user) {
        Habit habit = habitRepo.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!habit.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this habit");
        }

        habitRepo.delete(habit);
    }
}
