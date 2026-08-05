package com.transactionmonitoring.backend.repository;

import com.transactionmonitoring.backend.entity.Transaction;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>{
    List<Transaction> findByAccountIdAndTransactionDateAfter(String accountId, LocalDateTime transaction_date);
    long countByAccountIdAndPayeeId(String accountId, String payeeid);
}

