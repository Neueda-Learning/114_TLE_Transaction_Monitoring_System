package com.transactionmonitoring.backend.simulation;

import com.transactionmonitoring.backend.entity.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomTransactionGeneratorTest {

    private final RandomTransactionGenerator generator = new RandomTransactionGenerator();

    @Test
    void generate_createsTransaction() {
        Transaction tx = generator.generate();
        assertNotNull(tx);
        assertNotNull(tx.getAccountId());
    }

    @Test
    void generate_setsAccountId() {
        Transaction tx = generator.generate();
        assertTrue(tx.getAccountId().startsWith("ACC-"));
    }

    @Test
    void generate_setsAmount() {
        Transaction tx = generator.generate();
        assertTrue(tx.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void generate_setCurrency() {
        Transaction tx = generator.generate();
        assertNotNull(tx.getCurrency());
    }

    @Test
    void generate_setsTransactionType() {
        Transaction tx = generator.generate();
        assertNotNull(tx.getTransactionType());
    }

    @Test
    void generate_setsStatus() {
        Transaction tx = generator.generate();
        assertNotNull(tx.getStatus());
    }
}
