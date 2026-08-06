package com.transactionmonitoring.backend.repository;
import com.transactionmonitoring.backend.entity.Rules;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RulesRepository extends JpaRepository<Rules,Long> {
    Rules findByRuleTypeAndIsActiveTrue(String ruleType);
}
