package com.transactionmonitoring.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.transactionmonitoring.backend.service.TransactionService;
import org.springframework.web.bind.annotation.RequestBody;
import com.transactionmonitoring.backend.entity.Transaction;



@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }
    @PostMapping
    public Transaction saveTransaction(@RequestBody Transaction transaction){
        return transactionService.saveTransaction(transaction);
    }
    
    
    
}
