package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.transactionmonitoring.backend.entity.Transaction;
import java.util.List;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final AlertService alertService;
    private final TransactionRepository transactionRepository;
    private final RulesService rulesService;
    public TransactionService(TransactionRepository transactionRepository, RulesService rulesService, AlertService alertService){
        this.transactionRepository = transactionRepository;
        this.rulesService = rulesService;
        this.alertService = alertService;
    }

    public Transaction saveTransaction(Transaction transaction){
        Transaction savedTransaction = transactionRepository.save(transaction);
        List<String> violations = rulesService.checkRules(savedTransaction);
        alertService.createAlerts(savedTransaction, violations);

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
    
}
