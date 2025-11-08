package com.example.quickevent.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.quickevent.model.User;
import com.example.quickevent.repository.UserRepository;
import com.example.quickevent.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password required"));
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "username already taken"));
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "email already taken"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("ROLE_USER");
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User login) {
        try {
            Authentication a = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
            );

            String token = jwtUtil.generateToken(login.getUsername());
            return ResponseEntity.ok(Map.of(
                    "token", "Bearer " + token,
                    "expiresIn", jwtUtil.getExpirationMs()
            ));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "auth error"));
        }
    }
}
