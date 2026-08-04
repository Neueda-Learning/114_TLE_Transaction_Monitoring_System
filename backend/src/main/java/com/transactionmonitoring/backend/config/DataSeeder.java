package com.transactionmonitoring.backend.config;

import com.transactionmonitoring.backend.entity.User;
import com.transactionmonitoring.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Seeds two demo accounts (one per role) on first startup, since self-registration
// is disabled — accounts are provisioned internally, not created by users.
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setEmployeeId("EMP-ADMIN-01");
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setFullName("Alex Morgan");
                admin.setEmail("admin@trustmonitor.local");
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("analyst").isEmpty()) {
                User analyst = new User();
                analyst.setEmployeeId("EMP-ANALYST-01");
                analyst.setUsername("analyst");
                analyst.setPasswordHash(passwordEncoder.encode("analyst123"));
                analyst.setFullName("Riya Sharma");
                analyst.setEmail("analyst@trustmonitor.local");
                analyst.setRole("ANALYST");
                userRepository.save(analyst);
            }
        };
    }
}
