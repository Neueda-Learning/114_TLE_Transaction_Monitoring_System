package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.service.RulesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RulesControllerTest {

    private RulesService rulesService;
    private RulesController rulesController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        rulesService = mock(RulesService.class);
        rulesController = new RulesController(rulesService);
        mockMvc = MockMvcBuilders.standaloneSetup(rulesController).build();
    }

    @Test
    void getAllRules_returnsRules() throws Exception {
        Rules rule1 = new Rules();
        rule1.setRuleId(1L);
        rule1.setRuleName("Amount Threshold");

        Rules rule2 = new Rules();
        rule2.setRuleId(2L);
        rule2.setRuleName("Velocity Rule");

        when(rulesService.getAllRules()).thenReturn(List.of(rule1, rule2));

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk());

        verify(rulesService, times(1)).getAllRules();
    }

    @Test
    void getRuleById_returnsRule() throws Exception {
        Rules rule = new Rules();
        rule.setRuleId(1L);
        rule.setRuleName("Amount Threshold");

        when(rulesService.getRuleById(1L)).thenReturn(rule);

        mockMvc.perform(get("/api/rules/1"))
                .andExpect(status().isOk());

        verify(rulesService, times(1)).getRuleById(1L);
    }

    @Test
    void getRuleById_returnsNotFoundForNonExistentRule() throws Exception {
        when(rulesService.getRuleById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/rules/999"))
                .andExpect(status().isNotFound());

        verify(rulesService, times(1)).getRuleById(999L);
    }

    @Test
    void createRule_createsNewRule() throws Exception {
        Rules rule = new Rules();
        rule.setRuleId(1L);
        rule.setRuleName("New Rule");
        rule.setRuleType("AMOUNT_THRESHOLD");

        when(rulesService.saveRules(any(Rules.class))).thenReturn(rule);

        mockMvc.perform(post("/api/rules")
                        .contentType("application/json")
                        .content("{\"ruleName\": \"New Rule\", \"ruleType\": \"AMOUNT_THRESHOLD\"}"))
                .andExpect(status().isOk());

        verify(rulesService, times(1)).saveRules(any(Rules.class));
    }

    @Test
    void updateRule_updatesExistingRule() throws Exception {
        Rules updated = new Rules();
        updated.setRuleId(1L);
        updated.setRuleName("Updated Rule");

        when(rulesService.updateRule(eq(1L), any(Rules.class))).thenReturn(updated);

        mockMvc.perform(put("/api/rules/1")
                        .contentType("application/json")
                        .content("{\"ruleName\": \"Updated Rule\"}"))
                .andExpect(status().isOk());

        verify(rulesService, times(1)).updateRule(eq(1L), any(Rules.class));
    }

    @Test
    void updateRule_returnsNotFoundForNonExistentRule() throws Exception {
        when(rulesService.updateRule(eq(999L), any(Rules.class))).thenReturn(null);

        mockMvc.perform(put("/api/rules/999")
                        .contentType("application/json")
                        .content("{\"ruleName\": \"Updated Rule\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRule_deletesRule() throws Exception {
        when(rulesService.deleteRule(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/rules/1"))
                .andExpect(status().isNoContent());

        verify(rulesService, times(1)).deleteRule(1L);
    }

    @Test
    void deleteRule_returnsNotFoundForNonExistentRule() throws Exception {
        when(rulesService.deleteRule(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/rules/999"))
                .andExpect(status().isNotFound());

        verify(rulesService, times(1)).deleteRule(999L);
    }
}
