package com.transactionmonitoring.backend.config;

import com.transactionmonitoring.backend.entity.Alert;
import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.entity.Transaction;
import com.transactionmonitoring.backend.entity.User;
import com.transactionmonitoring.backend.repository.AlertRepository;
import com.transactionmonitoring.backend.repository.RulesRepository;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import com.transactionmonitoring.backend.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

// Seeds two demo accounts (one per role) and test transactions on first startup
@Configuration
@Profile("!test")
public class DataSeeder {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
    private static final String RULE_TYPE_BEHAVIORAL = "BEHAVIORAL";

    private final String adminSeedPassword;
    private final String analystSeedPassword;

    public DataSeeder(
            @Value("${app.seed.admin-password}") String adminSeedPassword,
            @Value("${app.seed.analyst-password}") String analystSeedPassword) {
        this.adminSeedPassword = adminSeedPassword;
        this.analystSeedPassword = analystSeedPassword;
    }

    private static LocalDateTime utcNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setEmployeeId("EMP-ADMIN-01");
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode(adminSeedPassword));
                admin.setFullName("Alex Morgan");
                admin.setEmail("admin@trustmonitor.local");
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("analyst").isEmpty()) {
                User analyst = new User();
                analyst.setEmployeeId("EMP-ANALYST-01");
                analyst.setUsername("analyst");
                analyst.setPasswordHash(passwordEncoder.encode(analystSeedPassword));
                analyst.setFullName("Riya Sharma");
                analyst.setEmail("analyst@trustmonitor.local");
                analyst.setRole("ANALYST");
                userRepository.save(analyst);
            }
        };
    }

    @Bean
    CommandLineRunner seedTransactions(TransactionRepository transactionRepository) {
        return args -> {
            if (transactionRepository.count() == 0) {
                Transaction txn1 = new Transaction();
                txn1.setAccountId("ACC-1001");
                txn1.setAmount(new BigDecimal("5000.00"));
                txn1.setCurrency("USD");
                txn1.setTransactionType("TRANSFER");
                txn1.setPayeeId("PYEE-001");
                txn1.setPayeeName("Amazon Inc");
                txn1.setFraudStatus("NORMAL");
                txn1.setInvestigationStatus(STATUS_OPEN);
                txn1.setTransactionDate(utcNow().minusHours(2));
                txn1.setStatus(STATUS_PENDING);
                transactionRepository.save(txn1);

                Transaction txn2 = new Transaction();
                txn2.setAccountId("ACC-1002");
                txn2.setAmount(new BigDecimal("25000.00"));
                txn2.setCurrency("USD");
                txn2.setTransactionType("WIRE_TRANSFER");
                txn2.setPayeeId("PYEE-002");
                txn2.setPayeeName("Unknown Merchant");
                txn2.setFraudStatus("SUSPICIOUS");
                txn2.setInvestigationStatus(STATUS_OPEN);
                txn2.setTransactionDate(utcNow().minusHours(1));
                txn2.setStatus(STATUS_PENDING);
                transactionRepository.save(txn2);

                Transaction txn3 = new Transaction();
                txn3.setAccountId("ACC-1003");
                txn3.setAmount(new BigDecimal("50000.00"));
                txn3.setCurrency("USD");
                txn3.setTransactionType("TRANSFER");
                txn3.setPayeeId("PYEE-003");
                txn3.setPayeeName("Crypto Exchange XYZ");
                txn3.setFraudStatus("FRAUDULENT");
                txn3.setInvestigationStatus(STATUS_OPEN);
                txn3.setTransactionDate(utcNow().minusMinutes(30));
                txn3.setStatus(STATUS_PENDING);
                transactionRepository.save(txn3);

                Transaction txn4 = new Transaction();
                txn4.setAccountId("ACC-1004");
                txn4.setAmount(new BigDecimal("150.00"));
                txn4.setCurrency("USD");
                txn4.setTransactionType("PURCHASE");
                txn4.setPayeeId("PYEE-004");
                txn4.setPayeeName("Local Store");
                txn4.setFraudStatus("NORMAL");
                txn4.setInvestigationStatus(STATUS_OPEN);
                txn4.setTransactionDate(utcNow().minusMinutes(15));
                txn4.setStatus(STATUS_PENDING);
                transactionRepository.save(txn4);
            }
        };
    }

    @Bean
    CommandLineRunner seedRules(RulesRepository rulesRepository) {
        return args -> {
            if (rulesRepository.count() == 0) {
                Rules rule1 = new Rules();
                rule1.setRuleName("High-Value Transaction Detector");
                rule1.setRuleType("AMOUNT_THRESHOLD");
                rule1.setFieldName("amount");
                rule1.setOperator("GREATER_THAN");
                rule1.setThresholdValue("20000");
                rule1.setIsActive(true);
                rule1.setCreatedAt(utcNow().minusDays(10));
                rulesRepository.save(rule1);

                Rules rule2 = new Rules();
                rule2.setRuleName("Crypto Exchange Monitor");
                rule2.setRuleType(RULE_TYPE_BEHAVIORAL);
                rule2.setFieldName("payeeName");
                rule2.setOperator("CONTAINS");
                rule2.setThresholdValue("Crypto");
                rule2.setIsActive(true);
                rule2.setCreatedAt(utcNow().minusDays(7));
                rulesRepository.save(rule2);

                Rules rule3 = new Rules();
                rule3.setRuleName("Unknown Merchant Alert");
                rule3.setRuleType(RULE_TYPE_BEHAVIORAL);
                rule3.setFieldName("payeeName");
                rule3.setOperator("CONTAINS");
                rule3.setThresholdValue("Unknown");
                rule3.setIsActive(true);
                rule3.setCreatedAt(utcNow().minusDays(5));
                rulesRepository.save(rule3);

                Rules rule4 = new Rules();
                rule4.setRuleName("Daily Velocity Limit");
                rule4.setRuleType("DAILY_LIMIT");
                rule4.setFieldName("amount");
                rule4.setOperator("GREATER_THAN");
                rule4.setThresholdValue("10000");
                rule4.setTimeWindowMinutes(1440);
                rule4.setIsActive(false);
                rule4.setCreatedAt(utcNow().minusDays(3));
                rulesRepository.save(rule4);

                Rules rule5 = new Rules();
                rule5.setRuleName("Fraud Threshold");
                rule5.setRuleType("FRAUD_THRESHOLD");
                rule5.setThresholdValue("70");
                rule5.setIsActive(true);
                rule5.setCreatedAt(utcNow().minusDays(1));
                rulesRepository.save(rule5);
            }
        };
    }

    @Bean
    CommandLineRunner seedAlerts(
            AlertRepository alertRepository,
            TransactionRepository transactionRepository,
            RulesRepository rulesRepository) {
        return args -> {
            List<Transaction> transactions = transactionRepository.findAll();
            List<Rules> rules = rulesRepository.findAll();

            if (transactions.size() < 4 || rules.size() < 4) {
                return;
            }

            transactions.sort(Comparator.comparing(Transaction::getTransactionId));
            rules.sort(Comparator.comparing(Rules::getRuleId));

            Transaction txn1 = transactions.get(0);
            Transaction txn2 = transactions.get(1);
            Transaction txn3 = transactions.get(2);
            Transaction txn4 = transactions.get(3);

            Rules rule1 = rules.get(0);
            Rules rule2 = rules.get(1);
            Rules rule3 = rules.get(2);
            Rules rule4 = rules.get(3);

            List<Alert> existingAlerts = alertRepository.findAll();

            if (existingAlerts.stream().noneMatch(a ->
                    txn1.getTransactionId().equals(a.getTransactionId())
                            && rule1.getRuleId().equals(a.getRuleId())
                            && "Transaction from high-risk customer - First transaction to new merchant".equals(a.getAlertMessage()))) {
                Alert alert1 = new Alert();
                alert1.setTransactionId(txn1.getTransactionId());
                alert1.setRuleId(rule1.getRuleId());
                alert1.setAlertType(RULE_TYPE_BEHAVIORAL);
                alert1.setSeverity("HIGH");
                alert1.setAlertStatus(STATUS_OPEN);
                alert1.setAlertMessage("Transaction from high-risk customer - First transaction to new merchant");
                alert1.setCreatedAt(utcNow().minusMinutes(5));
                alertRepository.save(alert1);
            }

            if (existingAlerts.stream().noneMatch(a ->
                    txn2.getTransactionId().equals(a.getTransactionId())
                            && rule2.getRuleId().equals(a.getRuleId())
                            && "Transaction exceeds daily limit - Amount $25,000 > Threshold $20,000".equals(a.getAlertMessage()))) {
                Alert alert2 = new Alert();
                alert2.setTransactionId(txn2.getTransactionId());
                alert2.setRuleId(rule2.getRuleId());
                alert2.setAlertType("AMOUNT_THRESHOLD");
                alert2.setSeverity("MEDIUM");
                alert2.setAlertStatus(STATUS_OPEN);
                alert2.setAlertMessage("Transaction exceeds daily limit - Amount $25,000 > Threshold $20,000");
                alert2.setCreatedAt(utcNow().minusMinutes(3));
                alertRepository.save(alert2);
            }

            if (existingAlerts.stream().noneMatch(a ->
                    txn3.getTransactionId().equals(a.getTransactionId())
                            && rule3.getRuleId().equals(a.getRuleId())
                            && "High-risk crypto exchange transaction detected - $50,000 to crypto service".equals(a.getAlertMessage()))) {
                Alert alert3 = new Alert();
                alert3.setTransactionId(txn3.getTransactionId());
                alert3.setRuleId(rule3.getRuleId());
                alert3.setAlertType(RULE_TYPE_BEHAVIORAL);
                alert3.setSeverity("HIGH");
                alert3.setAlertStatus("INVESTIGATING");
                alert3.setAlertMessage("High-risk crypto exchange transaction detected - $50,000 to crypto service");
                alert3.setCreatedAt(utcNow().minusMinutes(2));
                alertRepository.save(alert3);
            }

            if (existingAlerts.stream().noneMatch(a ->
                    txn4.getTransactionId().equals(a.getTransactionId())
                            && rule4.getRuleId().equals(a.getRuleId())
                            && "Routine low-value transaction - $150 purchase at local retail store".equals(a.getAlertMessage()))) {
                Alert alert4 = new Alert();
                alert4.setTransactionId(txn4.getTransactionId());
                alert4.setRuleId(rule4.getRuleId());
                alert4.setAlertType("ROUTINE");
                alert4.setSeverity("LOW");
                alert4.setAlertStatus(STATUS_ACKNOWLEDGED);
                alert4.setAlertMessage("Routine low-value transaction - $150 purchase at local retail store");
                alert4.setCreatedAt(utcNow().minusMinutes(1));
                alertRepository.save(alert4);
            }
        };
    }
}
