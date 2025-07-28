package com.example.HabitTracker.service;

import com.example.HabitTracker.model.User;
import com.example.HabitTracker.repository.UserRepository;
import com.example.HabitTracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String register(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) return null;

        // Use the constructor that only takes email and password (habits are null at registration)
        User user = new User(email, passwordEncoder.encode(password), name);
        userRepository.save(user);

        // Generate token using the user's email after registration
        return jwtUtil.generateToken(user.getEmail());
    }

    public String login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> jwtUtil.generateToken(u.getEmail())) // Use user.getEmail() for token generation
                .orElse(null);
    }
}
