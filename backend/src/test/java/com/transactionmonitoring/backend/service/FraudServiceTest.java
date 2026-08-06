package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.repository.RulesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FraudServiceTest {

    private RulesRepository rulesRepository;
    private FraudService fraudService;

    @BeforeEach
    void setUp() {
        rulesRepository = mock(RulesRepository.class);
        fraudService = new FraudService(rulesRepository);

        Rules fraudThreshold = new Rules();
        fraudThreshold.setRuleType("FRAUD_THRESHOLD");
        fraudThreshold.setThresholdValue("70");
        when(rulesRepository.findByRuleTypeAndIsActiveTrue("FRAUD_THRESHOLD"))
                .thenReturn(fraudThreshold);
    }

    @Test
    void classifyFraud_returnsFraudulentWhenScoreMeetsThreshold() {
        String result = fraudService.classifyFraud(List.of("AMOUNT_THRESHOLD", "VELOCITY"));

        assertEquals("FRAUDULENT", result);
    }

    @Test
    void classifyFraud_returnsSuspiciousForMidScore() {
        String result = fraudService.classifyFraud(List.of("AMOUNT_THRESHOLD"));

        assertEquals("SUSPICIOUS", result);
    }

    @Test
    void classifyFraud_returnsNormalForLowScore() {
        String result = fraudService.classifyFraud(List.of("NEW_PAYEE"));

        assertEquals("NORMAL", result);
    }
}
