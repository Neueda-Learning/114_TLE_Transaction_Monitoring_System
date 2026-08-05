package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.entity.Logs;
import com.transactionmonitoring.backend.service.AlertService;
import com.transactionmonitoring.backend.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;
    private final LogService logService;

    public AlertController(AlertService alertService, LogService logService) {
        this.alertService = alertService;
        this.logService = logService;
    }

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    @GetMapping("/{alertId}")
    public Alert getAlertById(@PathVariable Long alertId) {
        return alertService.getAlertById(alertId);
    }

    @PatchMapping("/{alertId}/status")
    public Alert updateAlertStatus(@PathVariable Long alertId,
                                   @RequestBody UpdateAlertStatusRequest request) {
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }

        try {
            return alertService.updateAlertStatus(
                    alertId,
                    request.getStatus().trim(),
                    request.getAction(),
                    request.getDescription());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/{alertId}/logs")
    public List<Logs> getAlertLogs(@PathVariable Long alertId) {
        return logService.getLogsByAlertId(alertId);
    }

    public static class UpdateAlertStatusRequest {
        private String status;
        private String action;
        private String description;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
