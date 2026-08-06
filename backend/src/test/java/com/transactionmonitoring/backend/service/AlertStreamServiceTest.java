package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

class AlertStreamServiceTest {

    private AlertStreamService alertStreamService;

    @BeforeEach
    void setUp() {
        alertStreamService = new AlertStreamService();
    }

    @Test
    void subscribe_returnsSseEmitter() {
        SseEmitter emitter = alertStreamService.subscribe();

        assertNotNull(emitter);
    }

    @Test
    void subscribe_multipleSubscribers() {
        SseEmitter emitter1 = alertStreamService.subscribe();
        SseEmitter emitter2 = alertStreamService.subscribe();
        SseEmitter emitter3 = alertStreamService.subscribe();

        assertNotNull(emitter1);
        assertNotNull(emitter2);
        assertNotNull(emitter3);
    }

    @Test
    void publishAlertCreated_doesNotThrowWithValidAlert() {
        Alert alert = new Alert();
        alert.setAlertId(1L);
        alert.setTransactionId(100L);
        alert.setRuleId(10L);
        alert.setAlertType("AMOUNT_THRESHOLD");
        alert.setSeverity("HIGH");
        alert.setAlertStatus("OPEN");
        alert.setAlertMessage("High amount detected");
        alert.setCreatedAt(LocalDateTime.now());

        // Subscribe first so there's at least one emitter
        SseEmitter emitter = alertStreamService.subscribe();

        // This should not throw any exception
        alertStreamService.publishAlertCreated(alert);
    }

    @Test
    void publishAlertCreated_handlesNullCreatedAt() {
        Alert alert = new Alert();
        alert.setAlertId(1L);
        alert.setTransactionId(100L);
        alert.setRuleId(10L);
        alert.setAlertType("VELOCITY");
        alert.setSeverity("MEDIUM");
        alert.setAlertStatus("OPEN");
        alert.setAlertMessage("Velocity rule triggered");
        alert.setCreatedAt(null);

        SseEmitter emitter = alertStreamService.subscribe();

        // Should handle null createdAt gracefully
        alertStreamService.publishAlertCreated(alert);
    }

    @Test
    void publishAlertCreated_withNoSubscribers() {
        Alert alert = new Alert();
        alert.setAlertId(1L);
        alert.setTransactionId(100L);
        alert.setRuleId(10L);
        alert.setAlertType("NEW_PAYEE");
        alert.setSeverity("LOW");
        alert.setAlertStatus("OPEN");
        alert.setAlertMessage("New payee detected");

        // No subscribers, should not throw
        alertStreamService.publishAlertCreated(alert);
    }

    @Test
    void publishAlertCreated_multipleAlerts() {
        SseEmitter emitter = alertStreamService.subscribe();

        for (int i = 1; i <= 5; i++) {
            Alert alert = new Alert();
            alert.setAlertId((long) i);
            alert.setTransactionId(100L + i);
            alert.setRuleId(10L);
            alert.setAlertType("AMOUNT_THRESHOLD");
            alert.setSeverity("HIGH");
            alert.setAlertStatus("OPEN");
            alert.setAlertMessage("Alert " + i);

            alertStreamService.publishAlertCreated(alert);
        }
    }
}
