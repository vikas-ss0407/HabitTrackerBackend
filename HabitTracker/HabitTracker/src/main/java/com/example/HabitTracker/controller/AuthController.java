package com.example.HabitTracker.controller;

import com.example.HabitTracker.dto.AuthRequest;
import com.example.HabitTracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty() ||
                request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: All fields (email, password, name) are required!");
        }

        String token = authService.register(request.getEmail(), request.getPassword(), request.getName());
        if (token == null) {
            return ResponseEntity.badRequest().body("Error: User with this email already exists!");
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Both email and password are required!");
        }

        String token = authService.login(request.getEmail(), request.getPassword());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid credentials!");
        }

        return ResponseEntity.ok(token);
    }
}
