package com.transactionmonitoring.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.transactionmonitoring.backend.service.TransactionService;
import org.springframework.web.bind.annotation.RequestBody;
import com.transactionmonitoring.backend.entity.Transaction;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;



@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping
    public Transaction saveTransaction(@RequestBody Transaction transaction){
        return transactionService.saveTransaction(transaction);
    }
    
    
    
}
