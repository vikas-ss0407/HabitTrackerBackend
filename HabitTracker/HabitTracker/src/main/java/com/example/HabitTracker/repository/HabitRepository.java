package com.example.HabitTracker.repository;

import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
}
