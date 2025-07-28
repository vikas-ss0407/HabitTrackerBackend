package com.example.HabitTracker.repository;

import com.example.HabitTracker.model.Note;
import com.example.HabitTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);
}