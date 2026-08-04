package com.transactionmonitoring.backend.dto;

public record UpdatePasswordRequest(String employeeId, String newPassword) {
}
