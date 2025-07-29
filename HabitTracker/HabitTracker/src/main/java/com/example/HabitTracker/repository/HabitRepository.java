package com.example.HabitTracker.repository;

import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
    Optional<Habit> findByUserAndName(User user, String name);

    @Query("SELECT h FROM Habit h WHERE h.user = :user AND " +
            "(h.createdDate IS NULL OR " +
            "YEAR(h.createdDate) < :year OR " +
            "(YEAR(h.createdDate) = :year AND MONTH(h.createdDate) <= :month))")
    List<Habit> findByUserAndCreatedBeforeOrDuringMonth(@Param("user") User user,
                                                        @Param("month") int month,
                                                        @Param("year") int year);

    @Modifying
    @Transactional
    @Query("DELETE FROM Habit h WHERE h.id = :habitId AND h.user.id = :userId")
    void deleteByIdAndUserId(@Param("habitId") Long habitId, @Param("userId") Long userId);
}
