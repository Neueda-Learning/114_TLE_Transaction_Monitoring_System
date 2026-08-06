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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

// Seeds two demo accounts (one per role) and test transactions on first startup
@Configuration
@Profile("!test")
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setEmployeeId("EMP-ADMIN-01");
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setFullName("Alex Morgan");
                admin.setEmail("admin@trustmonitor.local");
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("analyst").isEmpty()) {
                User analyst = new User();
                analyst.setEmployeeId("EMP-ANALYST-01");
                analyst.setUsername("analyst");
                analyst.setPasswordHash(passwordEncoder.encode("analyst123"));
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
                txn1.setInvestigationStatus("OPEN");
                txn1.setTransactionDate(LocalDateTime.now().minusHours(2));
                txn1.setStatus("PENDING");
                transactionRepository.save(txn1);

                Transaction txn2 = new Transaction();
                txn2.setAccountId("ACC-1002");
                txn2.setAmount(new BigDecimal("25000.00"));
                txn2.setCurrency("USD");
                txn2.setTransactionType("WIRE_TRANSFER");
                txn2.setPayeeId("PYEE-002");
                txn2.setPayeeName("Unknown Merchant");
                txn2.setFraudStatus("SUSPICIOUS");
                txn2.setInvestigationStatus("OPEN");
                txn2.setTransactionDate(LocalDateTime.now().minusHours(1));
                txn2.setStatus("PENDING");
                transactionRepository.save(txn2);

                Transaction txn3 = new Transaction();
                txn3.setAccountId("ACC-1003");
                txn3.setAmount(new BigDecimal("50000.00"));
                txn3.setCurrency("USD");
                txn3.setTransactionType("TRANSFER");
                txn3.setPayeeId("PYEE-003");
                txn3.setPayeeName("Crypto Exchange XYZ");
                txn3.setFraudStatus("FRAUDULENT");
                txn3.setInvestigationStatus("OPEN");
                txn3.setTransactionDate(LocalDateTime.now().minusMinutes(30));
                txn3.setStatus("PENDING");
                transactionRepository.save(txn3);

                Transaction txn4 = new Transaction();
                txn4.setAccountId("ACC-1004");
                txn4.setAmount(new BigDecimal("150.00"));
                txn4.setCurrency("USD");
                txn4.setTransactionType("PURCHASE");
                txn4.setPayeeId("PYEE-004");
                txn4.setPayeeName("Local Store");
                txn4.setFraudStatus("NORMAL");
                txn4.setInvestigationStatus("OPEN");
                txn4.setTransactionDate(LocalDateTime.now().minusMinutes(15));
                txn4.setStatus("PENDING");
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
                rule1.setCreatedAt(LocalDateTime.now().minusDays(10));
                rulesRepository.save(rule1);

                Rules rule2 = new Rules();
                rule2.setRuleName("Crypto Exchange Monitor");
                rule2.setRuleType("BEHAVIORAL");
                rule2.setFieldName("payeeName");
                rule2.setOperator("CONTAINS");
                rule2.setThresholdValue("Crypto");
                rule2.setIsActive(true);
                rule2.setCreatedAt(LocalDateTime.now().minusDays(7));
                rulesRepository.save(rule2);

                Rules rule3 = new Rules();
                rule3.setRuleName("Unknown Merchant Alert");
                rule3.setRuleType("BEHAVIORAL");
                rule3.setFieldName("payeeName");
                rule3.setOperator("CONTAINS");
                rule3.setThresholdValue("Unknown");
                rule3.setIsActive(true);
                rule3.setCreatedAt(LocalDateTime.now().minusDays(5));
                rulesRepository.save(rule3);

                Rules rule4 = new Rules();
                rule4.setRuleName("Daily Velocity Limit");
                rule4.setRuleType("DAILY_LIMIT");
                rule4.setFieldName("amount");
                rule4.setOperator("GREATER_THAN");
                rule4.setThresholdValue("10000");
                rule4.setTimeWindowMinutes(1440);
                rule4.setIsActive(false);
                rule4.setCreatedAt(LocalDateTime.now().minusDays(3));
                rulesRepository.save(rule4);

                Rules rule5 = new Rules();
                rule5.setRuleName("Fraud Threshold");
                rule5.setRuleType("FRAUD_THRESHOLD");
                rule5.setThresholdValue("70");
                rule5.setIsActive(true);
                rule5.setCreatedAt(LocalDateTime.now().minusDays(1));
                rulesRepository.save(rule5);
            }
        };
    }

    @Bean
    CommandLineRunner seedAlerts(AlertRepository alertRepository) {
        return args -> {
            if (alertRepository.count() == 0) {
                // Alert 1: HIGH severity, OPEN status
                Alert alert1 = new Alert();
                alert1.setTransactionId(1L);
                alert1.setRuleId(1L);
                alert1.setAlertType("BEHAVIORAL");
                alert1.setSeverity("HIGH");
                alert1.setAlertStatus("OPEN");
                alert1.setAlertMessage("Transaction from high-risk customer - First transaction to new merchant");
                alert1.setCreatedAt(LocalDateTime.now().minusMinutes(5));
                alertRepository.save(alert1);

                // Alert 2: MEDIUM severity, OPEN status
                Alert alert2 = new Alert();
                alert2.setTransactionId(2L);
                alert2.setRuleId(2L);
                alert2.setAlertType("AMOUNT_THRESHOLD");
                alert2.setSeverity("MEDIUM");
                alert2.setAlertStatus("OPEN");
                alert2.setAlertMessage("Transaction exceeds daily limit - Amount $25,000 > Threshold $20,000");
                alert2.setCreatedAt(LocalDateTime.now().minusMinutes(3));
                alertRepository.save(alert2);

                // Alert 3: HIGH severity, INVESTIGATING status
                Alert alert3 = new Alert();
                alert3.setTransactionId(3L);
                alert3.setRuleId(3L);
                alert3.setAlertType("BEHAVIORAL");
                alert3.setSeverity("HIGH");
                alert3.setAlertStatus("INVESTIGATING");
                alert3.setAlertMessage("High-risk crypto exchange transaction detected - $50,000 to crypto service");
                alert3.setCreatedAt(LocalDateTime.now().minusMinutes(2));
                alertRepository.save(alert3);

                // Alert 4: LOW severity, ACKNOWLEDGED status
                Alert alert4 = new Alert();
                alert4.setTransactionId(4L);
                alert4.setRuleId(4L);
                alert4.setAlertType("ROUTINE");
                alert4.setSeverity("LOW");
                alert4.setAlertStatus("ACKNOWLEDGED");
                alert4.setAlertMessage("Routine low-value transaction - $150 purchase at local retail store");
                alert4.setCreatedAt(LocalDateTime.now().minusMinutes(1));
                alertRepository.save(alert4);
            }
        };
    }
}
