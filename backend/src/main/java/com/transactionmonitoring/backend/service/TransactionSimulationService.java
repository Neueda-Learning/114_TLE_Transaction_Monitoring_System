package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.simulation.RandomTransactionGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionSimulationService {

    private static final Logger log = LoggerFactory.getLogger(TransactionSimulationService.class);

    private final TransactionService transactionService;
    private final RandomTransactionGenerator randomTransactionGenerator;

    public TransactionSimulationService(
            TransactionService transactionService,
            RandomTransactionGenerator randomTransactionGenerator) {
        this.transactionService = transactionService;
        this.randomTransactionGenerator = randomTransactionGenerator;
    }

    /**
     * Generates a single random transaction and persists it.
     *
     * @return the saved {@link Transaction} with its generated ID
     */
    @Transactional
    public Transaction generateAndSaveOne() {
        Transaction tx = randomTransactionGenerator.generate();
        Transaction saved = transactionService.saveTransaction(tx);
        log.info("Saved transaction id={} accountId={} amount={} {}",
                saved.getTransactionId(), saved.getAccountId(),
                saved.getAmount(), saved.getCurrency());
        return saved;
    }

    /**
     * Generates and persists {@code count} random transactions in a single
     * database transaction for efficiency.
     *
     * @param count number of transactions to generate; must be &gt; 0
     * @return list of saved {@link Transaction} objects
     * @throws IllegalArgumentException if {@code count} is less than 1
     */
    @Transactional
    public List<Transaction> generateAndSaveBatch(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1, got: " + count);
        }

        List<Transaction> saved = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Transaction tx = randomTransactionGenerator.generate();
            saved.add(transactionService.saveTransaction(tx));
        }

        log.info("Saved batch of {} transactions (rules evaluated per transaction)", saved.size());
        return saved;
    }

    /**
     * Generates transactions using deterministic scenarios so tests and demos
     * can exercise fraud rules, alerts, and rollback workflows predictably.
     */
    @Transactional
    public List<Transaction> generateCoverageBatch(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1, got: " + count);
        }

        List<Transaction> saved = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Transaction tx = createCoverageTransaction(i);
            saved.add(transactionService.saveTransaction(tx));
        }

        log.info("Saved coverage batch of {} transactions", saved.size());
        return saved;
    }

    private Transaction createCoverageTransaction(int index) {
        Transaction tx = new Transaction();
        int scenario = index % 4;

        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDateTime.now(ZoneOffset.UTC));
        tx.setStatus("SUCCESS");

        if (scenario == 0) {
            tx.setAccountId("ACC-COVER-1001");
            tx.setTransactionType("TRANSFER");
            tx.setAmount(new BigDecimal("450.00"));
            tx.setPayeeId("PAY-COVER-NORMAL");
            tx.setPayeeName("Coverage Normal Payee");
            return tx;
        }

        if (scenario == 1) {
            tx.setAccountId("ACC-COVER-1002");
            tx.setTransactionType("TRANSFER");
            tx.setAmount(new BigDecimal("25000.00"));
            tx.setPayeeId("PAY-COVER-HIGH-AMOUNT");
            tx.setPayeeName("Coverage High Amount Payee");
            return tx;
        }

        if (scenario == 2) {
            // Repeated account activity helps exercise velocity and daily-limit checks.
            tx.setAccountId("ACC-COVER-VELO");
            tx.setTransactionType("PAYMENT");
            tx.setAmount(new BigDecimal("7000.00"));
            tx.setPayeeId("PAY-COVER-VELO-" + index);
            tx.setPayeeName("Coverage Velocity Payee " + index);
            return tx;
        }

        tx.setAccountId("ACC-COVER-1003");
        tx.setTransactionType("WITHDRAWAL");
        tx.setAmount(new BigDecimal("12000.00"));
        tx.setPayeeId("PAY-COVER-DAILY");
        tx.setPayeeName("Coverage Daily Limit Payee");
        return tx;
    }
}
