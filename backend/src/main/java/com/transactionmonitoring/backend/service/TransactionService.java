package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.transactionmonitoring.backend.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import com.transactionmonitoring.backend.service.RulesService;
import com.transactionmonitoring.backend.service.AlertService;
import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.repository.AlertRepository;
import com.transactionmonitoring.backend.entity.Logs;
import com.transactionmonitoring.backend.service.LogService;
import java.util.stream.Collectors;
import com.transactionmonitoring.backend.service.FraudService;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final AlertService alertService;
    private final TransactionRepository transactionRepository;
    private final RulesService rulesService;
    private final AlertRepository alertRepository;
    private final FraudService fraudService;
    private final LogService logService;
    public TransactionService(TransactionRepository transactionRepository, RulesService rulesService, AlertService alertService, AlertRepository alertRepository, LogService logService,FraudService fraudService) {
        this.transactionRepository = transactionRepository;
        this.rulesService = rulesService;
        this.alertService = alertService;
        this.alertRepository = alertRepository;
        this.logService = logService;
        this.fraudService = fraudService;
    }

    public Transaction saveTransaction(Transaction transaction){
        transaction.setInvestigationStatus("SUCCESS");
        Transaction savedTransaction = transactionRepository.save(transaction);
        List<String> violations = rulesService.checkRules(savedTransaction);
        alertService.createAlerts(savedTransaction, violations);
        String fraudStatus =
        fraudService.classifyFraud(violations);

        transaction.setFraudStatus(fraudStatus);

        transactionRepository.save(transaction);
        return savedTransaction;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public void checkTransactionRules(Transaction transaction){
        List<String> violations = rulesService.checkRules(transaction);
        if(!violations.isEmpty()){
            violations.forEach(v ->
                log.warn("Violation found for transaction id={} rule={}", transaction.getTransactionId(), v)
            );
        } else {
            log.info("No violations found for transaction id={}", transaction.getTransactionId());
        }
    }

    public Transaction rollbackTransaction(Long transactionId){
        return rollbackTransaction(transactionId, null);
    }

    public Transaction rollbackTransaction(Long transactionId, String description){
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        List<Alert> alerts = alertRepository.findAllByTransactionId(transactionId);

        if(alerts.isEmpty()){
            throw new IllegalArgumentException("Alert not found for transaction");
        }

        List<Alert> investigatingAlerts = alerts.stream()
                .filter(alert -> "INVESTIGATING".equalsIgnoreCase(alert.getAlertStatus()))
                .collect(Collectors.toList());

        if(investigatingAlerts.isEmpty()){
            throw new IllegalStateException("Rollback allowed only when at least one alert is under investigation.");
        }

        transaction.setInvestigationStatus("ROLLED_BACK");
        transactionRepository.save(transaction);

        String refundAccountId = transaction.getPayeeId();
        if (refundAccountId == null || refundAccountId.isBlank()) {
            // Protect against DB NOT NULL constraint when original payee id is absent.
            refundAccountId = transaction.getAccountId();
            log.warn("Rollback id={} has null/blank payeeId. Using accountId={} for refund account.", transactionId, refundAccountId);
        }

        String refundPayeeId = transaction.getAccountId();
        if (refundPayeeId == null || refundPayeeId.isBlank()) {
            throw new IllegalStateException("Rollback failed: accountId is missing on original transaction");
        }

        Transaction refund = new Transaction();
        refund.setAccountId(refundAccountId);
        refund.setPayeeId(refundPayeeId);
        refund.setPayeeName("ORIGINAL ACCOUNT HOLDER");
        refund.setTransactionType("REFUND");
        refund.setAmount(transaction.getAmount());
        refund.setInvestigationStatus("REFUNDED");
        refund.setFraudStatus("NORMAL");
        refund.setStatus("SUCCESS");
        refund.setTransactionDate(LocalDateTime.now());
        refund.setCurrency(transaction.getCurrency());
        Transaction savedRefund = transactionRepository.save(refund);
        log.info("Rollback id={} created refund transaction id={}", transactionId, savedRefund.getTransactionId());
        for (Alert alert : investigatingAlerts) {
            String oldStatus = alert.getAlertStatus();
            alert.setAlertStatus("ROLLED_BACK");
            alertRepository.save(alert);

            Logs log = new Logs();
            log.setAlertId(alert.getAlertId());
            log.setAction("ROLLBACK");
            log.setOldStatus(oldStatus);
            log.setNewStatus("ROLLED_BACK");
            String trimmedDescription = description == null ? "" : description.trim();
            log.setDescription(
                    trimmedDescription.isEmpty()
                            ? "Transaction rolled back by analyst"
                            : "Transaction rolled back by analyst. Note: " + trimmedDescription);
            logService.saveLog(log);
        }

        return transaction;
    }
}
