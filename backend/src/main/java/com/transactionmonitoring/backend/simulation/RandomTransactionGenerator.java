package com.transactionmonitoring.backend.simulation;

import com.transactionmonitoring.backend.entity.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class RandomTransactionGenerator {

    private static final Random RANDOM = new Random();

    // Realistic account ID pool (simulates a bank with ~50 active accounts)
    private static final List<String> ACCOUNT_IDS = List.of(
            "ACC-1001", "ACC-1002", "ACC-1003", "ACC-1004", "ACC-1005",
            "ACC-1006", "ACC-1007", "ACC-1008", "ACC-1009", "ACC-1010",
            "ACC-2001", "ACC-2002", "ACC-2003", "ACC-2004", "ACC-2005",
            "ACC-3001", "ACC-3002", "ACC-3003", "ACC-3004", "ACC-3005",
            "ACC-4001", "ACC-4002", "ACC-4003", "ACC-4004", "ACC-4005"
    );

    private static final List<String> CURRENCIES = List.of(
            "USD", "EUR", "GBP", "INR", "AED", "SGD", "CAD", "AUD"
    );

    private static final List<String> TRANSACTION_TYPES = List.of(
            "TRANSFER", "WITHDRAWAL", "DEPOSIT", "PAYMENT", "REFUND", "POS_PURCHASE"
    );

    // Weighted status distribution: mostly COMPLETED, occasionally others
    private static final List<String> WEIGHTED_STATUSES = List.of(
            "COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED", "COMPLETED",
            "COMPLETED", "COMPLETED", "PENDING", "PENDING", "FAILED"
    );

    private static final List<String> PAYEE_NAMES = List.of(
            "Amazon", "Walmart", "Netflix", "Google Pay", "Apple Store",
            "Uber", "Shell Gas Station", "Local Pharmacy", "Supermarket",
            "Electric Utility Co.", "Insurance Corp", "Rent Payment",
            "John Smith", "Jane Doe", "Robert Johnson", "Emily Davis",
            "Global Remit Ltd", "FastPay Transfer", "International Wire"
    );

    /**
     * Generates a single realistic banking transaction with randomised fields.
     *
     * @return a populated {@link Transaction} (transactionId is null until persisted)
     */
    public Transaction generate() {
        Transaction tx = new Transaction();

        tx.setAccountId(randomFrom(ACCOUNT_IDS));
        tx.setAmount(generateAmount());
        tx.setCurrency(randomFrom(CURRENCIES));
        tx.setTransactionType(randomFrom(TRANSACTION_TYPES));
        tx.setPayeeId(generatePayeeId());
        tx.setPayeeName(randomFrom(PAYEE_NAMES));
        tx.setTransactionDate(generateTransactionDate());
        tx.setStatus(randomFrom(WEIGHTED_STATUSES));

        return tx;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private BigDecimal generateAmount() {
        int roll = RANDOM.nextInt(100);
        double raw;
        if (roll < 70) {
            raw = 1 + RANDOM.nextDouble() * 499;
        } else if (roll < 90) {
            raw = 500 + RANDOM.nextDouble() * 4_500;
        } else if (roll < 98) {
            raw = 5_000 + RANDOM.nextDouble() * 45_000;
        } else {
            raw = 50_000 + RANDOM.nextDouble() * 450_000;
        }
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }

    private String generatePayeeId() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return "PAY-" + uuid;
    }

    private LocalDateTime generateTransactionDate() {
        long secondsBack = (long) (RANDOM.nextDouble() * 86_400); // up to 24 h ago
        return LocalDateTime.now().minusSeconds(secondsBack);
    }

    private <T> T randomFrom(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
}
