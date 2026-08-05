package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import com.transactionmonitoring.backend.simulation.RandomTransactionGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionSimulationService {

    private static final Logger log = LoggerFactory.getLogger(TransactionSimulationService.class);

    private final TransactionRepository transactionRepository;
    private final RandomTransactionGenerator randomTransactionGenerator;

    public TransactionSimulationService(
            TransactionRepository transactionRepository,
            RandomTransactionGenerator randomTransactionGenerator) {
        this.transactionRepository = transactionRepository;
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
        Transaction saved = transactionRepository.save(tx);
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

        List<Transaction> batch = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            batch.add(randomTransactionGenerator.generate());
        }

        List<Transaction> saved = transactionRepository.saveAll(batch);
        log.info("Saved batch of {} transactions", saved.size());
        return saved;
    }
}
