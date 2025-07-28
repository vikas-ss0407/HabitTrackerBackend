package com.example.HabitTracker.controller;

import com.example.HabitTracker.model.User;
import com.example.HabitTracker.repository.UserRepository;
import com.example.HabitTracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token missing or malformed");
        }

        String jwt = token.replace("Bearer ", "").replace("bearer ", "");
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("name", user.getName());

        // Format the joined date
        String formattedDate = user.getJoinedDate() != null
                ? user.getJoinedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : "Unknown";
        profile.put("joinedDate", formattedDate);

        return ResponseEntity.ok(profile);
    }
}
