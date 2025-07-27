package com.example.HabitTracker.controller;

import com.example.HabitTracker.dto.AuthRequest;
import com.example.HabitTracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty() ||
                request.getName() == null || request.getName().isEmpty()) {
            return "Error: All fields (email, password, name) are required!";
        }

        String token = authService.register(request.getEmail(), request.getPassword(), request.getName());
        if (token == null) {
            return "Error: User with this email already exists!";
        }

        return token;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return "Error: Both email and password are required!";
        }

        String token = authService.login(request.getEmail(), request.getPassword());
        if (token == null) {
            return "Error: Invalid credentials!";
        }

        return token;
    }
}
