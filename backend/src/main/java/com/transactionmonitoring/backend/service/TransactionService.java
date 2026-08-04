package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.transactionmonitoring.backend.entity.Transaction;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    public Transaction saveTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }
}
