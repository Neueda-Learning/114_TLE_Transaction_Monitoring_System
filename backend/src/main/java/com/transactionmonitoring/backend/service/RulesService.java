package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.RulesRepository;
import com.transactionmonitoring.backend.entity.Rules;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class RulesService {
    private final RulesRepository rulesRepository;
    public RulesService(RulesRepository rulesRepository){
        this.rulesRepository = rulesRepository;
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
}
