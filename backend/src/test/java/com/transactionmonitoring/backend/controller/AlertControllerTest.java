package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.entity.Logs;
import com.transactionmonitoring.backend.service.AlertService;
import com.transactionmonitoring.backend.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlertControllerTest {

    private AlertService alertService;
    private LogService logService;
    private AlertController alertController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        alertService = mock(AlertService.class);
        logService = mock(LogService.class);
        alertController = new AlertController(alertService, logService);
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
    }

    @Test
    void getAllAlerts_returnsAlerts() throws Exception {
        Alert alert1 = new Alert();
        alert1.setAlertId(1L);
        alert1.setAlertType("AMOUNT_THRESHOLD");

        Alert alert2 = new Alert();
        alert2.setAlertId(2L);
        alert2.setAlertType("VELOCITY");

        when(alertService.getAllAlerts()).thenReturn(List.of(alert1, alert2));

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk());

        verify(alertService, times(1)).getAllAlerts();
    }

    @Test
    void getAlertById_returnsAlert() throws Exception {
        Alert alert = new Alert();
        alert.setAlertId(1L);
        alert.setAlertType("AMOUNT_THRESHOLD");
        alert.setSeverity("HIGH");

        when(alertService.getAlertById(1L)).thenReturn(alert);

        mockMvc.perform(get("/api/alerts/1"))
                .andExpect(status().isOk());

        verify(alertService, times(1)).getAlertById(1L);
    }

    @Test
    void updateAlertStatus_updatesAlert() throws Exception {
        Alert alert = new Alert();
        alert.setAlertId(1L);
        alert.setAlertStatus("CLOSED");

        when(alertService.updateAlertStatus(eq(1L), eq("CLOSED"), any(), any())).thenReturn(alert);

        mockMvc.perform(patch("/api/alerts/1/status")
                        .contentType("application/json")
                        .content("{\"status\": \"CLOSED\", \"action\": \"RESOLVED\", \"description\": \"False alarm\"}"))
                .andExpect(status().isOk());

        verify(alertService, times(1)).updateAlertStatus(eq(1L), eq("CLOSED"), any(), any());
    }

    @Test
    void updateAlertStatus_returnsBadRequestForMissingStatus() throws Exception {
        mockMvc.perform(patch("/api/alerts/1/status")
                        .contentType("application/json")
                        .content("{\"action\": \"RESOLVED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAlertLogs_returnsLogsForAlert() throws Exception {
        Logs log1 = new Logs();
        log1.setLogId(1L);
        log1.setAlertId(1L);

        Logs log2 = new Logs();
        log2.setLogId(2L);
        log2.setAlertId(1L);

        when(logService.getLogsByAlertId(1L)).thenReturn(List.of(log1, log2));

        mockMvc.perform(get("/api/alerts/1/logs"))
                .andExpect(status().isOk());

        verify(logService, times(1)).getLogsByAlertId(1L);
    }
}
