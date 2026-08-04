package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.RulesRepository;
import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.entity.Transaction;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.time.LocalDate;
@Service
public class RulesService {
    private final RulesRepository rulesRepository;
    private final TransactionRepository transactionRepository;
    public RulesService(RulesRepository rulesRepository, TransactionRepository transactionRepository){
        this.rulesRepository = rulesRepository;
        this.transactionRepository = transactionRepository;
    }
    public Rules saveRules(Rules rules){
        return rulesRepository.save(rules);
    }

    public List<Rules> getAllRules() {
        return rulesRepository.findAll();
    }

    public Rules getRuleById(Long id) {
        return rulesRepository.findById(id).orElse(null);
    }

    public Rules updateRule(Long id, Rules updated) {
        Rules existing = rulesRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setRuleName(updated.getRuleName());
        existing.setRuleType(updated.getRuleType());
        existing.setFieldName(updated.getFieldName());
        existing.setOperator(updated.getOperator());
        existing.setThresholdValue(updated.getThresholdValue());
        existing.setTimeWindowMinutes(updated.getTimeWindowMinutes());
        existing.setIsActive(updated.getIsActive());

        return rulesRepository.save(existing);
    }

    public boolean deleteRule(Long id) {
        if (!rulesRepository.existsById(id)) {
            return false;
        }

        rulesRepository.deleteById(id);
        return true;
    }

    public List<String> checkRules(Transaction transaction){
        List<String> violations = new ArrayList<>();
        checkAmountThreshold(transaction,violations);
        checkVelocity(transaction,violations);
        checkNewPayee(transaction,violations);
        checkDailyLimit(transaction,violations);
        return violations;
    }
    //Amount Threshold Rule
    private void checkAmountThreshold(Transaction transaction,List<String> violations){
        // List<String> violations = new ArrayList<>();
        Rules amountRule = rulesRepository.findByRuleTypeAndIsActiveTrue("AMOUNT_THRESHOLD");
        if(amountRule == null){
            return;
        }
        BigDecimal threshold = new BigDecimal(amountRule.getThresholdValue());
        if(transaction.getAmount().compareTo(threshold) > 0){
            violations.add("(Amount_Threshold)Transaction amount exceeds the threshold of " + threshold);
        }
        return;
    }
    //checkVelocity Rule
    private void checkVelocity(Transaction transaction,List<String> violations){
        // List<String> violations = new ArrayList<>();
        Rules velocityRule = rulesRepository.findByRuleTypeAndIsActiveTrue("VELOCITY");
        if(velocityRule == null){
            return;
        }
        int threshold = Integer.parseInt(velocityRule.getThresholdValue());
        int minutes = velocityRule.getTimeWindowMinutes();
        LocalDateTime timelimit = LocalDateTime.now().minusMinutes(minutes);

        List<Transaction> recentTransactions = transactionRepository.findByAccountIdAndTransactionDateAfter(transaction.getAccountId(), timelimit);
        if(recentTransactions.size() > threshold){
            violations.add("VELOCITY" );
        }
    }
    private void checkNewPayee(Transaction transaction,List<String> violations){
        Rules newPayeeRule = rulesRepository.findByRuleTypeAndIsActiveTrue("NEW_PAYEE");
        if(newPayeeRule == null){
            return;
        }
        long count = transactionRepository.countByAccountIdAndPayeeId(transaction.getAccountId(), transaction.getPayeeId());
        if(count == 1){
            violations.add("NEW_PAYEE");
        }
    }
    private void checkDailyLimit(Transaction transaction,List<String> violations){
        Rules dailyRule = rulesRepository.findByRuleTypeAndIsActiveTrue("DAILY_LIMIT");
        if(dailyRule == null){
            return;
        }
        List<Transaction> transactions = transactionRepository.findByAccountIdAndTransactionDateAfter(transaction.getAccountId(), LocalDate.now().atStartOfDay());
        BigDecimal total = BigDecimal.ZERO;
        for(Transaction t:transactions){
            total = total.add(t.getAmount());
        }
        BigDecimal threshold = new BigDecimal(dailyRule.getThresholdValue());
        if(total.compareTo(threshold) > 0){
            violations.add("DAILY_LIMIT");
        }
    }
}
