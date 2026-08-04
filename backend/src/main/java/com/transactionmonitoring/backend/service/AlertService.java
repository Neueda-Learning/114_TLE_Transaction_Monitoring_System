package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.repository.AlertRepository;
import com.transactionmonitoring.backend.entity.Alert;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    private final AlertRepository alertRepository;
    public AlertService(AlertRepository alertRepository){
        this.alertRepository = alertRepository;
    }

    public Alert saveAlert(Alert alert){
        return alertRepository.save(alert);
    }

}
