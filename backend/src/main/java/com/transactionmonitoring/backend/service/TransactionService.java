package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.transactionmonitoring.backend.entity.Transaction;
import java.util.List;

@Service
public class TransactionService {
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
    public void checkTransactionRules(Transaction transaction){
        List<String> violations =rulesService.checkRules(transaction);
        if(!violations.isEmpty()){
            for(String violation : violations){
                System.out.println("Violation found for transaction: " + transaction.getTransactionId() + " - " + violation);
            }
            
        }
        else{
            System.out.println("No violations found for transaction: " + transaction.getTransactionId());
        }
    }
    
}
