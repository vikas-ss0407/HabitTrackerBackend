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

        User user = new User(null, email, passwordEncoder.encode(password), name);
        userRepository.save(user);

        return jwtUtil.generateToken(email);
    }

    public String login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> jwtUtil.generateToken(email))
                .orElse(null);
    }
}
