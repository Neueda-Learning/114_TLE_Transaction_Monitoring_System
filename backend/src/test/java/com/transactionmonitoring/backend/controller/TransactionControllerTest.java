package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerTest {

    private TransactionService transactionService;
    private TransactionController transactionController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        transactionController = new TransactionController(transactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void getAllTransactions_returnsTransactions() throws Exception {
        Transaction tx1 = new Transaction();
        tx1.setTransactionId(1L);
        tx1.setAccountId("ACC-1");

        Transaction tx2 = new Transaction();
        tx2.setTransactionId(2L);
        tx2.setAccountId("ACC-2");

        when(transactionService.getAllTransactions()).thenReturn(List.of(tx1, tx2));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    void saveTransaction_savesNewTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId("ACC-1");
        transaction.setPayeeId("PAY-1");
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setCurrency("USD");
        transaction.setTransactionType("TRANSFER");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        Transaction saved = new Transaction();
        saved.setTransactionId(1L);
        saved.setAccountId("ACC-1");
        saved.setPayeeId("PAY-1");
        saved.setAmount(new BigDecimal("1000.00"));

        when(transactionService.saveTransaction(any(Transaction.class))).thenReturn(saved);

        mockMvc.perform(post("/api/transactions")
                        .contentType("application/json")
                        .content("{\"accountId\": \"ACC-1\", \"payeeId\": \"PAY-1\", \"amount\": 1000.00, \"currency\": \"USD\", \"transactionType\": \"TRANSFER\", \"status\": \"SUCCESS\"}"))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).saveTransaction(any(Transaction.class));
    }

    @Test
    void rollbackTransaction_rollsBackTransaction() throws Exception {
        Transaction original = new Transaction();
        original.setTransactionId(1L);
        original.setAccountId("ACC-1");
        original.setPayeeId("PAY-1");
        original.setAmount(new BigDecimal("1000.00"));

        Transaction rolledBack = new Transaction();
        rolledBack.setTransactionId(2L);
        rolledBack.setTransactionType("REFUND");

        when(transactionService.rollbackTransaction(1L)).thenReturn(rolledBack);

        mockMvc.perform(patch("/api/transactions/1/rollback"))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).rollbackTransaction(1L);
    }

    @Test
    void rollbackTransaction_returnsNotFoundForNonExistentTransaction() throws Exception {
        when(transactionService.rollbackTransaction(999L)).thenThrow(new IllegalArgumentException("Transaction not found"));

        mockMvc.perform(patch("/api/transactions/999/rollback"))
                .andExpect(status().isNotFound());

        verify(transactionService, times(1)).rollbackTransaction(999L);
    }

    @Test
    void rollbackTransaction_returnsBadRequestWhenAlreadyRefunded() throws Exception {
        when(transactionService.rollbackTransaction(1L)).thenThrow(new IllegalStateException("Transaction already refunded"));

        mockMvc.perform(patch("/api/transactions/1/rollback"))
                .andExpect(status().isBadRequest());

        verify(transactionService, times(1)).rollbackTransaction(1L);
    }
}
