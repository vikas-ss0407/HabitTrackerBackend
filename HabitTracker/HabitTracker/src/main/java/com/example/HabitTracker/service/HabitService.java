package com.example.HabitTracker.service;

import com.example.HabitTracker.dto.HabitRequest;
import com.example.HabitTracker.dto.HabitUpdateRequest;
import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import com.example.HabitTracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitService {
    private final HabitRepository habitRepo;

    public List<Habit> getHabits(User user) {
        return habitRepo.findByUser(user);
    }

    public List<Habit> getHabitsForMonth(User user, Integer month, Integer year) {
        if (month == null || year == null) {
            return habitRepo.findByUser(user);
        }

        List<Habit> allHabits = habitRepo.findByUser(user);
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        return allHabits.stream()
                .filter(habit -> {
                    LocalDateTime createdDate = habit.getCreatedDate();
                    if (createdDate == null) {
                        createdDate = today;
                    }

                    int habitDurationDays = habit.getProgress() != null ? habit.getProgress().size() : 21;

                    LocalDateTime habitStartDate = createdDate.isAfter(today) ? createdDate : today;
                    LocalDateTime habitEndDate = createdDate.plusDays(habitDurationDays - 1);

                    LocalDateTime monthStart = LocalDateTime.of(year, month, 1, 0, 0);
                    LocalDateTime monthEnd = monthStart.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

                    return !habitStartDate.isAfter(monthEnd) && !habitEndDate.isBefore(monthStart) && !habitEndDate.isBefore(today);
                })
                .collect(Collectors.toList());
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

        habit.setName(habitRequest.getName());
        habit.setPurpose(habitRequest.getPurpose());

        if (habitRequest.getProgress() != null && !habitRequest.getProgress().isEmpty()) {
            if (habitRequest.getProgress().size() != habit.getProgress().size()) {
                List<Boolean> newProgress = new ArrayList<>();
                int minSize = Math.min(habit.getProgress().size(), habitRequest.getProgress().size());

                for (int i = 0; i < minSize; i++) {
                    newProgress.add(habit.getProgress().get(i));
                }

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
        System.out.println("HabitService: Attempting to delete habit ID: " + habitId + " for user: " + user.getEmail());

        Habit habit = habitRepo.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        System.out.println("HabitService: Found habit: " + habit.getName() + " owned by user: " + habit.getUser().getEmail());

        if (!habit.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this habit");
        }

        System.out.println("HabitService: Authorization check passed, deleting habit from database");

        habitRepo.deleteByIdAndUserId(habitId, user.getId());

        habitRepo.flush();

        boolean stillExists = habitRepo.existsById(habitId);
        if (stillExists) {
            throw new RuntimeException("Failed to delete habit from database");
        }

        System.out.println("HabitService: Habit deleted from database successfully and verified");
    }
}
