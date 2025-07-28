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
@CrossOrigin(origins = "http://localhost:5173")
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

        // Use the service to create habit properly
        Habit savedHabit = habitService.createHabit(habit, user);
        return ResponseEntity.ok(savedHabit);
    }

    @GetMapping
    public ResponseEntity<List<Habit>> getHabits(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            // Return an empty list and a FORBIDDEN status if the token is missing or malformed
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Habit> habits = habitRepository.findByUser(user);
        return ResponseEntity.ok(habits);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateHabitProgress(@RequestBody Map<String, Object> request, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token missing or malformed");
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String habitName = (String) request.get("habitName");
        int day = (Integer) request.get("day") - 1; // Convert to 0-based index
        boolean status = (Boolean) request.get("status");

        Habit habit = habitRepository.findByUserAndName(user, habitName)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        List<Boolean> progress = habit.getProgress();
        if (day >= 0 && day < progress.size()) {
            progress.set(day, status);
            habitRepository.save(habit);
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
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token missing or malformed");
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        try {
            habitService.deleteHabit(id, user);
            return ResponseEntity.ok("Habit deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
