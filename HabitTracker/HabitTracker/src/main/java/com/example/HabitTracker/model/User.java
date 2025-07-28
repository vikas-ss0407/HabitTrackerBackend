package com.example.HabitTracker.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "habits")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;

    @Column(name = "joined_date")
    private LocalDateTime joinedDate;

    @PrePersist
    protected void onCreate() {
        if (joinedDate == null) {
            joinedDate = LocalDateTime.now();
        }
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Habit> habits;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.joinedDate = LocalDateTime.now();
        this.habits = null;
    }
}
