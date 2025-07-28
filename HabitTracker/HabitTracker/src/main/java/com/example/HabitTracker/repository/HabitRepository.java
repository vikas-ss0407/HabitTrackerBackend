package com.example.HabitTracker.repository;

import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
    Optional<Habit> findByUserAndName(User user, String name);
}
