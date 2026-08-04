package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;


    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }


    // Save a new alert
    public Alert saveAlert(Alert alert) {

        // Default status when alert is created
        if(alert.getAlertStatus() == null){
        alert.setAlertStatus("OPEN");
}

        // Set creation time
        if (alert.getCreatedAt() == null) {
            alert.setCreatedAt(LocalDateTime.now());
        }

        return alertRepository.save(alert);
    }


    // Get all alerts
    public List<Alert> getAllAlerts() {

        return alertRepository.findAll();
    }


    // Get alert by ID
    public Alert getAlertById(Long alertId) {

        return alertRepository.findById(alertId)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found with id: " + alertId)
                );
    }


    // Update alert status
    public Alert updateAlertStatus(Long alertId, String status) {

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found with id: " + alertId)
                );

        alert.setAlertStatus(status);

        return alertRepository.save(alert);
    }


    // Delete alert (optional)
    public void deleteAlert(Long alertId) {

        alertRepository.deleteById(alertId);
    }
}
