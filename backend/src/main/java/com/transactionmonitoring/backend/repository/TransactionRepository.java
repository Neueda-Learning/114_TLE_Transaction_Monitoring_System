package com.transactionmonitoring.backend.repository;

import com.transactionmonitoring.backend.entity.Transaction;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long>{
    List<Transaction> findByAccountIdAndTransactionDateAfter(String accountId, LocalDateTime transaction_date);
    long countByAccountIdAndPayeeId(String accountId, String payeeid);
}

