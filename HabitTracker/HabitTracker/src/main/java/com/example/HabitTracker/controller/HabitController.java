package com.example.HabitTracker.controller;


import com.example.HabitTracker.model.Habit;
import com.example.HabitTracker.model.User;
import com.example.HabitTracker.repository.HabitRepository;
import com.example.HabitTracker.repository.UserRepository;
import com.example.HabitTracker.security.JwtUtil;
import com.example.HabitTracker.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
public class HabitController {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final HabitService habitService;

    @PostMapping
    public ResponseEntity<?> addHabit(@RequestBody Habit habit, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token missing or malformed");
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Habit savedHabit = habitService.createHabit(habit, user);
        return ResponseEntity.ok(savedHabit);
    }

    @GetMapping
    public ResponseEntity<List<Habit>> getHabits(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Habit> habits = habitService.getHabitsForMonth(user, month, year);
        return ResponseEntity.ok(habits);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateHabitProgress(@RequestBody Map<String, Object> request, @RequestHeader("Authorization") String token) {
        System.out.println("Progress update request received: " + request);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token missing or malformed");
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String habitName = (String) request.get("habitName");
        int day = (Integer) request.get("day") - 1;
        boolean status = (Boolean) request.get("status");

        System.out.println("Updating habit: " + habitName + ", day: " + (day + 1) + ", status: " + status);

        Habit habit = habitRepository.findByUserAndName(user, habitName)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        System.out.println("Found habit: " + habit.getName() + " with progress size: " + habit.getProgress().size());

        List<Boolean> progress = habit.getProgress();
        if (day >= 0 && day < progress.size()) {
            Boolean oldStatus = progress.get(day);
            progress.set(day, status);

            System.out.println("Updated day " + (day + 1) + " from " + oldStatus + " to " + status);

            Habit savedHabit = habitRepository.save(habit);

            habitRepository.flush();

            System.out.println("Progress saved to database. New progress: " + savedHabit.getProgress());
        } else {
            System.out.println("Invalid day index: " + day + " for progress size: " + progress.size());
            return ResponseEntity.badRequest().body("Invalid day index");
        }

        return ResponseEntity.ok("Progress updated");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHabit(@PathVariable Long id, @RequestBody Habit habitRequest, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token missing or malformed");
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Habit updatedHabit = habitService.updateHabit(id, habitRequest, user);
            return ResponseEntity.ok(updatedHabit);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        System.out.println("Delete request received for habit ID: " + id);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token missing or malformed");
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User authenticated: " + email);

        try {
            habitService.deleteHabit(id, user);
            System.out.println("Habit deleted successfully from database: " + id);
            return ResponseEntity.ok("Habit deleted successfully");
        } catch (RuntimeException e) {
            System.out.println("Error deleting habit: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
