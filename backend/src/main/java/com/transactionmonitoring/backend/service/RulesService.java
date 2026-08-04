package com.transactionmonitoring.backend.service;
import com.transactionmonitoring.backend.repository.RulesRepository;
import com.transactionmonitoring.backend.entity.Rules;
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
}
