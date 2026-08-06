package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.entity.Logs;
import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.repository.AlertRepository;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private RulesService rulesService;
    private AlertService alertService;
    private AlertRepository alertRepository;
    private LogService logService;
    private FraudService fraudService;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        rulesService = mock(RulesService.class);
        alertService = mock(AlertService.class);
        alertRepository = mock(AlertRepository.class);
        logService = mock(LogService.class);
        fraudService = mock(FraudService.class);

        transactionService = new TransactionService(
                transactionRepository,
                rulesService,
                alertService,
                alertRepository,
                logService,
                fraudService
        );
    }

    @Test
    void saveTransaction_setsFraudStatusAndCreatesAlerts() {
        Transaction input = new Transaction();
        input.setAccountId("ACC-1");
        input.setPayeeId("PAY-1");
        input.setAmount(new BigDecimal("25000.00"));
        input.setCurrency("USD");
        input.setTransactionType("TRANSFER");
        input.setTransactionDate(LocalDateTime.now());
        input.setStatus("SUCCESS");

        AtomicLong nextId = new AtomicLong(10);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            if (tx.getTransactionId() == null) {
                tx.setTransactionId(nextId.getAndIncrement());
            }
            return tx;
        });
        when(rulesService.checkRules(any(Transaction.class))).thenReturn(List.of("AMOUNT_THRESHOLD", "NEW_PAYEE"));
        when(fraudService.classifyFraud(any())).thenReturn("FRAUDULENT");

        Transaction saved = transactionService.saveTransaction(input);

        assertEquals("SUCCESS", saved.getInvestigationStatus());
        assertEquals("FRAUDULENT", input.getFraudStatus());
        verify(alertService, times(1)).createAlerts(eq(saved), eq(List.of("AMOUNT_THRESHOLD", "NEW_PAYEE")));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void saveTransaction_keepsFailedStatusAndStillUpdatesFraudStatus() {
        Transaction input = new Transaction();
        input.setAccountId("ACC-FAILED");
        input.setPayeeId("PAY-FAILED");
        input.setAmount(new BigDecimal("500.00"));
        input.setCurrency("USD");
        input.setTransactionType("TRANSFER");
        input.setTransactionDate(LocalDateTime.now());
        input.setStatus("FAILED");

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(rulesService.checkRules(any(Transaction.class))).thenReturn(List.of("NEW_PAYEE"));
        when(fraudService.classifyFraud(any())).thenReturn("NORMAL");

        Transaction saved = transactionService.saveTransaction(input);

        assertEquals("FAILED", saved.getStatus());
        assertEquals("SUCCESS", saved.getInvestigationStatus());
        assertEquals("NORMAL", saved.getFraudStatus());
        verify(alertService, times(1)).createAlerts(eq(saved), eq(List.of("NEW_PAYEE")));
    }

    @Test
    void rollbackTransaction_createsRefundAndAuditLog() {
        Transaction original = new Transaction();
        original.setTransactionId(42L);
        original.setAccountId("ACC-ORIG");
        original.setPayeeId("PAY-ORIG");
        original.setAmount(new BigDecimal("1250.00"));
        original.setCurrency("USD");
        original.setInvestigationStatus("SUCCESS");

        Alert investigating = new Alert();
        investigating.setAlertId(99L);
        investigating.setTransactionId(42L);
        investigating.setAlertStatus("INVESTIGATING");

        when(transactionRepository.findById(42L)).thenReturn(Optional.of(original));
        when(alertRepository.findAllByTransactionId(42L)).thenReturn(List.of(investigating));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction rolledBack = transactionService.rollbackTransaction(42L);

        assertEquals("ROLLED_BACK", rolledBack.getInvestigationStatus());

        ArgumentCaptor<Transaction> txSaveCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txSaveCaptor.capture());
        List<Transaction> saves = txSaveCaptor.getAllValues();
        Transaction refund = saves.get(1);

        assertEquals("REFUND", refund.getTransactionType());
        assertEquals("PAY-ORIG", refund.getAccountId());
        assertEquals("ACC-ORIG", refund.getPayeeId());
        assertEquals("REFUNDED", refund.getInvestigationStatus());
        assertEquals("NORMAL", refund.getFraudStatus());

        assertEquals("ROLLED_BACK", investigating.getAlertStatus());

        ArgumentCaptor<Logs> logCaptor = ArgumentCaptor.forClass(Logs.class);
        verify(logService, times(1)).saveLog(logCaptor.capture());
        assertEquals("ROLLBACK", logCaptor.getValue().getAction());
    }

    @Test
    void rollbackTransaction_usesFallbackAccountWhenPayeeMissing() {
        Transaction original = new Transaction();
        original.setTransactionId(77L);
        original.setAccountId("ACC-FALLBACK");
        original.setPayeeId(null);
        original.setAmount(new BigDecimal("800.00"));
        original.setCurrency("USD");

        Alert investigating = new Alert();
        investigating.setAlertId(100L);
        investigating.setTransactionId(77L);
        investigating.setAlertStatus("INVESTIGATING");

        when(transactionRepository.findById(77L)).thenReturn(Optional.of(original));
        when(alertRepository.findAllByTransactionId(77L)).thenReturn(List.of(investigating));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.rollbackTransaction(77L);

        ArgumentCaptor<Transaction> txSaveCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txSaveCaptor.capture());
        Transaction refund = txSaveCaptor.getAllValues().get(1);

        assertEquals("ACC-FALLBACK", refund.getAccountId());
    }

    @Test
    void rollbackTransaction_throwsWhenNoInvestigatingAlert() {
        Transaction original = new Transaction();
        original.setTransactionId(50L);

        Alert closed = new Alert();
        closed.setAlertId(1L);
        closed.setTransactionId(50L);
        closed.setAlertStatus("OPEN");

        when(transactionRepository.findById(50L)).thenReturn(Optional.of(original));
        when(alertRepository.findAllByTransactionId(50L)).thenReturn(List.of(closed));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> transactionService.rollbackTransaction(50L)
        );

        assertTrue(ex.getMessage().contains("Rollback allowed only"));
    }

    @Test
    void rollbackTransaction_throwsWhenNoAlertsExist() {
        Transaction original = new Transaction();
        original.setTransactionId(51L);

        when(transactionRepository.findById(51L)).thenReturn(Optional.of(original));
        when(alertRepository.findAllByTransactionId(51L)).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.rollbackTransaction(51L)
        );

        assertTrue(ex.getMessage().contains("Alert not found"));
    }

    @Test
    void rollbackTransaction_throwsWhenOriginalAccountIdMissing() {
        Transaction original = new Transaction();
        original.setTransactionId(88L);
        original.setAccountId(" ");
        original.setPayeeId("PAY-ORIG");
        original.setAmount(new BigDecimal("430.00"));
        original.setCurrency("USD");

        Alert investigating = new Alert();
        investigating.setAlertId(101L);
        investigating.setTransactionId(88L);
        investigating.setAlertStatus("INVESTIGATING");

        when(transactionRepository.findById(88L)).thenReturn(Optional.of(original));
        when(alertRepository.findAllByTransactionId(88L)).thenReturn(List.of(investigating));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> transactionService.rollbackTransaction(88L)
        );

        assertTrue(ex.getMessage().contains("accountId is missing"));
    }
}
