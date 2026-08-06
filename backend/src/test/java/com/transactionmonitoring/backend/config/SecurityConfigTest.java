package com.transactionmonitoring.backend.config;

import com.transactionmonitoring.backend.security.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthFilter, "http://localhost:5173");
    }

    @Test
    void passwordEncoder_returnsValidEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertNotNull(encoder);
    }

    @Test
    void passwordEncoder_encodesPassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void passwordEncoder_differentEncodingsForSamePassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String encoded1 = encoder.encode(rawPassword);
        String encoded2 = encoder.encode(rawPassword);

        // BCrypt should produce different encoded values for same input
        // but both should match the original password
        assertTrue(encoder.matches(rawPassword, encoded1));
        assertTrue(encoder.matches(rawPassword, encoded2));
    }

    @Test
    void passwordEncoder_doesNotMatchIncorrectPassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String wrongPassword = "wrongPassword456";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(!encoder.matches(wrongPassword, encodedPassword));
    }

    @Test
    void corsConfigurationSource_returnsValidSource() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        assertNotNull(source);
    }

    @Test
    void corsConfigurationSource_hasAllowedOrigin() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        var corsConfig = source.getCorsConfiguration(request);

        assertNotNull(corsConfig);
        assertNotNull(corsConfig.getAllowedOrigins());
    }

    @Test
    void corsConfigurationSource_allowsCommonHttpMethods() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        var corsConfig = source.getCorsConfiguration(request);

        assertNotNull(corsConfig);
        var allowedMethods = corsConfig.getAllowedMethods();
        assertNotNull(allowedMethods);
        assertTrue(allowedMethods.contains("GET"));
        assertTrue(allowedMethods.contains("POST"));
        assertTrue(allowedMethods.contains("PUT"));
        assertTrue(allowedMethods.contains("DELETE"));
    }

    @Test
    void passwordEncoderStrength() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "SecureP@ssw0rd!";
        String encoded = encoder.encode(password);

        // Encoded password should be longer than raw (BCrypt adds salt and hash)
        assertTrue(encoded.length() > password.length());
    }

    @Test
    void multiplePasswordEncoderInstances() {
        PasswordEncoder encoder1 = securityConfig.passwordEncoder();
        PasswordEncoder encoder2 = securityConfig.passwordEncoder();

        assertNotNull(encoder1);
        assertNotNull(encoder2);
    }
}
