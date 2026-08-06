package com.transactionmonitoring.backend.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EntityTest {

    @Test
    void alert_createsWithId() {
        Alert alert = new Alert();
        alert.setAlertId(1L);
        assertEquals(1L, alert.getAlertId());
    }

    @Test
    void transaction_createsWithAccountId() {
        Transaction tx = new Transaction();
        tx.setAccountId("ACC-001");
        assertEquals("ACC-001", tx.getAccountId());
    }

    @Test
    void rule_createsWithRuleName() {
        Rules rule = new Rules();
        rule.setRuleName("Test Rule");
        assertEquals("Test Rule", rule.getRuleName());
    }

    @Test
    void log_createsWithLogId() {
        Logs log = new Logs();
        log.setLogId(1L);
        assertEquals(1L, log.getLogId());
    }

    @Test
    void user_createsWithUserId() {
        User user = new User();
        user.setUserId(1L);
        assertEquals(1L, user.getUserId());
    }
}
