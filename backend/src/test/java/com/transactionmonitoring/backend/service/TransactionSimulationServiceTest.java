package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.simulation.RandomTransactionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionSimulationServiceTest {

    private TransactionService transactionService;
    private RandomTransactionGenerator randomTransactionGenerator;
    private TransactionSimulationService simulationService;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        randomTransactionGenerator = mock(RandomTransactionGenerator.class);
        simulationService = new TransactionSimulationService(transactionService, randomTransactionGenerator);

        AtomicLong nextId = new AtomicLong(1000);
        when(transactionService.saveTransaction(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            if (tx.getTransactionId() == null) {
                tx.setTransactionId(nextId.getAndIncrement());
            }
            return tx;
        });

        Transaction randomTx = new Transaction();
        randomTx.setAccountId("ACC-RANDOM");
        randomTx.setPayeeId("PAY-RANDOM");
        randomTx.setPayeeName("Random Payee");
        randomTx.setAmount(new BigDecimal("123.45"));
        randomTx.setCurrency("USD");
        randomTx.setTransactionType("TRANSFER");
        randomTx.setStatus("SUCCESS");
        randomTx.setTransactionDate(LocalDateTime.now());
        when(randomTransactionGenerator.generate()).thenReturn(randomTx);
    }

    @Test
    void generateAndSaveBatch_savesRequestedCount() {
        List<Transaction> saved = simulationService.generateAndSaveBatch(3);

        assertEquals(3, saved.size());
        verify(randomTransactionGenerator, times(3)).generate();
        verify(transactionService, times(3)).saveTransaction(any(Transaction.class));
    }

    @Test
    void generateAndSaveBatch_throwsForInvalidCount() {
        assertThrows(IllegalArgumentException.class, () -> simulationService.generateAndSaveBatch(0));
    }

    @Test
    void generateAndSaveOne_supportsFailedTransactionsFromGenerator() {
        Transaction failedTx = new Transaction();
        failedTx.setAccountId("ACC-FAILED-GEN");
        failedTx.setPayeeId("PAY-FAILED-GEN");
        failedTx.setPayeeName("Failed Generator Payee");
        failedTx.setAmount(new BigDecimal("99.00"));
        failedTx.setCurrency("USD");
        failedTx.setTransactionType("TRANSFER");
        failedTx.setStatus("FAILED");
        failedTx.setTransactionDate(LocalDateTime.now());
        when(randomTransactionGenerator.generate()).thenReturn(failedTx);

        Transaction saved = simulationService.generateAndSaveOne();

        assertEquals("FAILED", saved.getStatus());
        verify(transactionService, times(1)).saveTransaction(any(Transaction.class));
    }

    @Test
    void generateCoverageBatch_createsCoverageTransactions() {
        List<Transaction> saved = simulationService.generateCoverageBatch(8);

        assertEquals(8, saved.size());
        verify(transactionService, times(8)).saveTransaction(any(Transaction.class));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionService, times(8)).saveTransaction(captor.capture());

        List<Transaction> generated = captor.getAllValues();
        long highAmountCount = generated.stream()
                .filter(tx -> tx.getAmount() != null && tx.getAmount().compareTo(new BigDecimal("20000")) > 0)
                .count();

        assertEquals(2, highAmountCount);
    }
}
