package com.transactionmonitoring.backend.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "this-is-a-very-long-secret-key-for-testing-purposes-only");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L); // 1 hour
    }

    @Test
    void generateToken_createsValidToken() {
        String token = jwtUtil.generateToken("EMP123", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void generateToken_withDifferentRoles() {
        String adminToken = jwtUtil.generateToken("EMP123", "ADMIN");
        String analystToken = jwtUtil.generateToken("EMP456", "ANALYST");
        String auditorToken = jwtUtil.generateToken("EMP789", "AUDITOR");

        assertNotNull(adminToken);
        assertNotNull(analystToken);
        assertNotNull(auditorToken);
    }

    @Test
    void parseClaims_extractsTokenData() {
        String token = jwtUtil.generateToken("EMP123", "ADMIN");
        Claims claims = jwtUtil.parseClaims(token);

        assertNotNull(claims);
        assertEquals("EMP123", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void parseClaims_extractsMultipleRoles() {
        String token = jwtUtil.generateToken("EMP456", "ANALYST");
        Claims claims = jwtUtil.parseClaims(token);

        assertEquals("EMP456", claims.getSubject());
        assertEquals("ANALYST", claims.get("role"));
    }

    @Test
    void isValid_returnsTrueForValidToken() {
        String token = jwtUtil.generateToken("EMP123", "ADMIN");

        assertTrue(jwtUtil.isValid(token));
    }

    @Test
    void isValid_returnsFalseForInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtUtil.isValid(invalidToken));
    }

    @Test
    void isValid_returnsFalseForMalformedToken() {
        String malformedToken = "malformed";

        assertFalse(jwtUtil.isValid(malformedToken));
    }

    @Test
    void isValid_returnsFalseForEmptyToken() {
        assertFalse(jwtUtil.isValid(""));
    }

    @Test
    void generateToken_createsValidTokenStructure() {
        String token = jwtUtil.generateToken("EMP123", "ADMIN");
        
        assertNotNull(token);
        // JWT should have 3 parts separated by dots (header.payload.signature)
        String[] parts = token.split("\\.");
        assertTrue(parts.length == 3, "Valid JWT should have 3 parts");
        
        // Each part should not be empty
        for (String part : parts) {
            assertNotNull(part);
            assertTrue(part.length() > 0, "JWT part should not be empty");
        }
    }

    @Test
    void parseClaimsAndIssuedDate() {
        String token = jwtUtil.generateToken("EMP123", "ADMIN");
        Claims claims = jwtUtil.parseClaims(token);

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}
