package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.repository.AlertRepository;
import com.transactionmonitoring.backend.repository.RulesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlertServiceTest {

    private AlertRepository alertRepository;
    private RulesRepository rulesRepository;
    private LogService logService;
    private AlertStreamService alertStreamService;
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertRepository = mock(AlertRepository.class);
        rulesRepository = mock(RulesRepository.class);
        logService = mock(LogService.class);
        alertStreamService = mock(AlertStreamService.class);
        alertService = new AlertService(alertRepository, rulesRepository, logService, alertStreamService);
    }

    @Test
    void createAlerts_createsMultipleAlertsForViolations() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setAccountId("ACC-1");

        Rules amountRule = new Rules();
        amountRule.setRuleId(1L);
        amountRule.setRuleType("AMOUNT_THRESHOLD");
        amountRule.setIsActive(true);

        Rules velocityRule = new Rules();
        velocityRule.setRuleId(2L);
        velocityRule.setRuleType("VELOCITY");
        velocityRule.setIsActive(true);

        when(rulesRepository.findByRuleTypeAndIsActiveTrue("AMOUNT_THRESHOLD")).thenReturn(amountRule);
        when(rulesRepository.findByRuleTypeAndIsActiveTrue("VELOCITY")).thenReturn(velocityRule);
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> {
            Alert alert = invocation.getArgument(0);
            alert.setAlertId(System.currentTimeMillis());
            return alert;
        });

        alertService.createAlerts(transaction, List.of("AMOUNT_THRESHOLD", "VELOCITY"));

        verify(alertRepository, times(2)).save(any(Alert.class));
        verify(alertStreamService, times(2)).publishAlertCreated(any(Alert.class));
    }

    @Test
    void createAlerts_skipsNullRules() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1L);

        when(rulesRepository.findByRuleTypeAndIsActiveTrue("UNKNOWN_RULE")).thenReturn(null);
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertService.createAlerts(transaction, List.of("UNKNOWN_RULE"));

        verify(alertRepository, times(0)).save(any(Alert.class));
    }

    @Test
    void createAlerts_setsCorrectSeverityLevels() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1L);

        Rules rule = new Rules();
        rule.setRuleId(1L);
        rule.setRuleType("AMOUNT_THRESHOLD");
        rule.setIsActive(true);

        when(rulesRepository.findByRuleTypeAndIsActiveTrue("AMOUNT_THRESHOLD")).thenReturn(rule);
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> {
            Alert alert = invocation.getArgument(0);
            alert.setAlertId(1L);
            return alert;
        });

        alertService.createAlerts(transaction, List.of("AMOUNT_THRESHOLD"));

        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void createAlerts_emptyViolationsList() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1L);

        alertService.createAlerts(transaction, List.of());

        verify(alertRepository, times(0)).save(any(Alert.class));
    }
}
