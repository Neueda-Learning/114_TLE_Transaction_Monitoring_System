package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.dto.LoginRequest;
import com.transactionmonitoring.backend.dto.ForgotPasswordRequest;
import com.transactionmonitoring.backend.dto.UpdatePasswordRequest;
import com.transactionmonitoring.backend.entity.User;
import com.transactionmonitoring.backend.repository.UserRepository;
import com.transactionmonitoring.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthController authController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        authController = new AuthController(userRepository, passwordEncoder, jwtUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_successfulLogin() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setFullName("Test User");
        user.setRole("ANALYST");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "ANALYST")).thenReturn("jwt-token-here");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\": \"testuser\", \"password\": \"password123\"}"))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "$2a$10$hashedpassword");
        verify(jwtUtil, times(1)).generateToken("testuser", "ANALYST");
    }

    @Test
    void login_failsWithIncorrectPassword() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("$2a$10$hashedpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$hashedpassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\": \"testuser\", \"password\": \"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "$2a$10$hashedpassword");
    }

    @Test
    void login_failsWithNonExistentUser() throws Exception {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\": \"nonexistent\", \"password\": \"password123\"}"))
                .andExpect(status().isUnauthorized());

        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void login_admin() throws Exception {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash("$2a$10$hashedpassword");
        admin.setFullName("Admin User");
        admin.setRole("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("admin123", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ADMIN")).thenReturn("admin-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\": \"admin\", \"password\": \"admin123\"}"))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).findByUsername("admin");
    }
}
