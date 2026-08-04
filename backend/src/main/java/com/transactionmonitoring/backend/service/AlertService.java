package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.repository.RulesRepository;
import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final RulesRepository ruleRepository;

    public AlertService(AlertRepository alertRepository,
                        RulesRepository ruleRepository) {
        this.alertRepository = alertRepository;
        this.ruleRepository = ruleRepository;
    }

    public void createAlerts(Transaction transaction,
                             List<String> violations) {

        for (String violation : violations) {

            Rules rule =
                    ruleRepository.findByRuleTypeAndIsActiveTrue(violation);

            if (rule == null) {
                continue;
            }

            Alert alert = new Alert();

            alert.setTransactionId(transaction.getTransactionId());
            alert.setRuleId(rule.getRuleId());
            alert.setAlertType(violation);
            alert.setAlertStatus("OPEN");

            alert.setSeverity(getSeverity(violation));

            alert.setAlertMessage(getMessage(violation, transaction));

            alertRepository.save(alert);
        }
    }

    private String getSeverity(String violation) {

        switch (violation) {

            case "AMOUNT_THRESHOLD":
                return "HIGH";

            case "VELOCITY":
                return "MEDIUM";

            case "NEW_PAYEE":
                return "LOW";

            case "DAILY_LIMIT":
                return "HIGH";

            default:
                return "LOW";
        }
    }

    private String getMessage(String violation,
                              Transaction transaction) {

        switch (violation) {

            case "AMOUNT_THRESHOLD":
                return "Transaction amount exceeded threshold.";

            case "VELOCITY":
                return "High number of transactions detected.";

            case "NEW_PAYEE":
                return "Transaction made to a new payee.";

            case "DAILY_LIMIT":
                return "Daily transaction limit exceeded.";

            default:
                return "Rule violated.";
        }
    }
}