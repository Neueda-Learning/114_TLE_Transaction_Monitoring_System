package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.transactionmonitoring.backend.entity.Transaction;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final RulesService rulesService;
    public TransactionService(TransactionRepository transactionRepository, RulesService rulesService){
        this.transactionRepository = transactionRepository;
        this.rulesService = rulesService;
    }

    public Transaction saveTransaction(Transaction transaction){
        Transaction savedTransaction = transactionRepository.save(transaction);
        checkTransactionRules(savedTransaction);
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
