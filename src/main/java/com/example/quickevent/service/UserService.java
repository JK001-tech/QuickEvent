package com.example.quickevent.service;

import com.example.quickevent.model.User;
import com.example.quickevent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register new user (with password encoding)
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("ROLE_USER");
        return userRepository.save(user);
    }

    // Fetch all users (for admin or debug)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get single user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Delete user (for admin use)
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Check if username/email already taken
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
