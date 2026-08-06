package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.repository.RulesRepository;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RulesServiceTest {

    private RulesRepository rulesRepository;
    private TransactionRepository transactionRepository;
    private RulesService rulesService;

    @BeforeEach
    void setUp() {
        rulesRepository = mock(RulesRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        rulesService = new RulesService(rulesRepository, transactionRepository);
    }

    @Test
    void saveRules_savesNewRule() {
        Rules rule = new Rules();
        rule.setRuleName("High Amount Rule");
        rule.setRuleType("AMOUNT_THRESHOLD");
        rule.setThresholdValue("10000");
        rule.setIsActive(true);

        when(rulesRepository.save(any(Rules.class))).thenAnswer(invocation -> {
            Rules r = invocation.getArgument(0);
            r.setRuleId(1L);
            return r;
        });

        Rules saved = rulesService.saveRules(rule);

        assertNotNull(saved);
        assertEquals(1L, saved.getRuleId());
        verify(rulesRepository, times(1)).save(any(Rules.class));
    }

    @Test
    void getAllRules_returnsAllRules() {
        Rules rule1 = new Rules();
        rule1.setRuleId(1L);
        rule1.setRuleName("Rule 1");

        Rules rule2 = new Rules();
        rule2.setRuleId(2L);
        rule2.setRuleName("Rule 2");

        when(rulesRepository.findAll()).thenReturn(List.of(rule1, rule2));

        List<Rules> rules = rulesService.getAllRules();

        assertEquals(2, rules.size());
        verify(rulesRepository, times(1)).findAll();
    }

    @Test
    void getRuleById_returnsRuleWhenExists() {
        Rules rule = new Rules();
        rule.setRuleId(1L);
        rule.setRuleName("Test Rule");

        when(rulesRepository.findById(1L)).thenReturn(Optional.of(rule));

        Rules found = rulesService.getRuleById(1L);

        assertNotNull(found);
        assertEquals("Test Rule", found.getRuleName());
    }

    @Test
    void getRuleById_returnsNullWhenNotExists() {
        when(rulesRepository.findById(999L)).thenReturn(Optional.empty());

        Rules found = rulesService.getRuleById(999L);

        assertNull(found);
    }

    @Test
    void updateRule_updatesExistingRule() {
        Rules existing = new Rules();
        existing.setRuleId(1L);
        existing.setRuleName("Old Name");
        existing.setThresholdValue("5000");

        Rules updated = new Rules();
        updated.setRuleName("New Name");
        updated.setThresholdValue("10000");

        when(rulesRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(rulesRepository.save(any(Rules.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rules result = rulesService.updateRule(1L, updated);

        assertEquals("New Name", result.getRuleName());
        assertEquals("10000", result.getThresholdValue());
        verify(rulesRepository, times(1)).save(any(Rules.class));
    }

    @Test
    void updateRule_returnsNullWhenRuleNotFound() {
        Rules updated = new Rules();
        updated.setRuleName("New Name");

        when(rulesRepository.findById(999L)).thenReturn(Optional.empty());

        Rules result = rulesService.updateRule(999L, updated);

        assertNull(result);
    }

    @Test
    void deleteRule_deletesExistingRule() {
        when(rulesRepository.existsById(1L)).thenReturn(true);

        boolean deleted = rulesService.deleteRule(1L);

        assertTrue(deleted);
        verify(rulesRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRule_returnsFalseWhenRuleNotFound() {
        when(rulesRepository.existsById(999L)).thenReturn(false);

        boolean deleted = rulesService.deleteRule(999L);

        assertFalse(deleted);
        verify(rulesRepository, times(0)).deleteById(any());
    }

    @Test
    void checkRules_skipsRefundTransactions() {
        Transaction refundTx = new Transaction();
        refundTx.setTransactionType("REFUND");

        List<String> violations = rulesService.checkRules(refundTx);

        assertTrue(violations.isEmpty());
    }
}
