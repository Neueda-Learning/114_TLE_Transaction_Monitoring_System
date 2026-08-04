package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.dto.ForgotPasswordRequest;
import com.transactionmonitoring.backend.dto.LoginRequest;
import com.transactionmonitoring.backend.dto.UpdatePasswordRequest;
import com.transactionmonitoring.backend.entity.User;
import com.transactionmonitoring.backend.repository.UserRepository;
import com.transactionmonitoring.backend.security.JwtUtil;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password."));
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "role", user.getRole(),
                "user", Map.of(
                        "username", user.getUsername(),
                        "name", user.getFullName(),
                        "role", user.getRole()
                )
        ));
    }

    // Simplified, training-project forgot-password flow: verify the Employee ID exists,
    // then allow setting a new BCrypt-hashed password directly. No OTP/email/token involved.
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        boolean exists = userRepository.findByEmployeeId(request.employeeId()).isPresent();

        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No account found for that Employee ID."));
        }

        return ResponseEntity.ok(Map.of("message", "Employee ID verified. You may set a new password."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody UpdatePasswordRequest request) {
        User user = userRepository.findByEmployeeId(request.employeeId()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No account found for that Employee ID."));
        }

        if (request.newPassword() == null || request.newPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "New password must be at least 6 characters."));
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password has been updated successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userRepository.findByUsername(authentication.getName())
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(Map.of(
                        "username", user.getUsername(),
                        "name", user.getFullName(),
                        "role", user.getRole()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
