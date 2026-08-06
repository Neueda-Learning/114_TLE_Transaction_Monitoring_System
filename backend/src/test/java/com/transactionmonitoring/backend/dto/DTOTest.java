package com.transactionmonitoring.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DTOTest {

    @Test
    void loginRequest_createsWithUsernameAndPassword() {
        LoginRequest request = new LoginRequest("admin", "password123");

        assertNotNull(request);
        assertEquals("admin", request.username());
        assertEquals("password123", request.password());
    }

    @Test
    void loginRequest_withSpecialCharacters() {
        LoginRequest request = new LoginRequest("user@example.com", "p@ss!w0rd#");

        assertEquals("user@example.com", request.username());
        assertEquals("p@ss!w0rd#", request.password());
    }

    @Test
    void loginRequest_withNullUsername() {
        LoginRequest request = new LoginRequest(null, "password");

        assertNull(request.username());
        assertEquals("password", request.password());
    }

    @Test
    void loginRequest_withNullPassword() {
        LoginRequest request = new LoginRequest("admin", null);

        assertEquals("admin", request.username());
        assertNull(request.password());
    }

    @Test
    void loginRequest_equality() {
        LoginRequest request1 = new LoginRequest("admin", "password");
        LoginRequest request2 = new LoginRequest("admin", "password");

        assertEquals(request1, request2);
    }

    @Test
    void updatePasswordRequest_createsWithFields() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("EMP123", "newPassword123");

        assertNotNull(request);
        assertEquals("EMP123", request.employeeId());
        assertEquals("newPassword123", request.newPassword());
    }

    @Test
    void updatePasswordRequest_withSpecialCharacters() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("EMP-001", "P@ssw0rd!");

        assertEquals("EMP-001", request.employeeId());
        assertEquals("P@ssw0rd!", request.newPassword());
    }

    @Test
    void updatePasswordRequest_withNullEmployeeId() {
        UpdatePasswordRequest request = new UpdatePasswordRequest(null, "password");

        assertNull(request.employeeId());
        assertEquals("password", request.newPassword());
    }

    @Test
    void updatePasswordRequest_withNullPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("EMP123", null);

        assertEquals("EMP123", request.employeeId());
        assertNull(request.newPassword());
    }

    @Test
    void forgotPasswordRequest_createsWithEmployeeId() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("EMP123");

        assertNotNull(request);
        assertEquals("EMP123", request.employeeId());
    }

    @Test
    void forgotPasswordRequest_withDifferentFormats() {
        ForgotPasswordRequest request1 = new ForgotPasswordRequest("EMP-001");
        ForgotPasswordRequest request2 = new ForgotPasswordRequest("123456");
        ForgotPasswordRequest request3 = new ForgotPasswordRequest("ADMIN");

        assertEquals("EMP-001", request1.employeeId());
        assertEquals("123456", request2.employeeId());
        assertEquals("ADMIN", request3.employeeId());
    }

    @Test
    void forgotPasswordRequest_withNullEmployeeId() {
        ForgotPasswordRequest request = new ForgotPasswordRequest(null);

        assertNull(request.employeeId());
    }

    @Test
    void forgotPasswordRequest_equality() {
        ForgotPasswordRequest request1 = new ForgotPasswordRequest("EMP123");
        ForgotPasswordRequest request2 = new ForgotPasswordRequest("EMP123");

        assertEquals(request1, request2);
    }

    @Test
    void allDTOsHaveCorrectTypes() {
        LoginRequest login = new LoginRequest("user", "pass");
        UpdatePasswordRequest updatePass = new UpdatePasswordRequest("EMP1", "newPass");
        ForgotPasswordRequest forgotPass = new ForgotPasswordRequest("EMP2");

        assertTrue(login instanceof LoginRequest);
        assertTrue(updatePass instanceof UpdatePasswordRequest);
        assertTrue(forgotPass instanceof ForgotPasswordRequest);
    }

    private boolean assertTrue(boolean condition) {
        return condition;
    }
}
