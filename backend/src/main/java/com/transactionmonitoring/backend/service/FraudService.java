package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Rules;
import com.transactionmonitoring.backend.repository.RulesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FraudService {

    private static final int DEFAULT_FRAUD_THRESHOLD = 70;

    private final RulesRepository rulesRepository;

    public FraudService(RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    public String classifyFraud(List<String> violations) {

        int score = 0;

        for(String violation : violations){

            switch(violation){

                case "AMOUNT_THRESHOLD":
                    score += 40;
                    break;

                case "VELOCITY":
                    score += 30;
                    break;

                case "DAILY_LIMIT":
                    score += 20;
                    break;

                case "NEW_PAYEE":
                    score += 10;
                    break;
            }
        }

        Rules fraudRule =
                rulesRepository.findByRuleTypeAndIsActiveTrue("FRAUD_THRESHOLD");

        int threshold = DEFAULT_FRAUD_THRESHOLD;
        if (fraudRule != null && fraudRule.getThresholdValue() != null) {
            try {
                threshold = Integer.parseInt(fraudRule.getThresholdValue());
            } catch (NumberFormatException ignored) {
                threshold = DEFAULT_FRAUD_THRESHOLD;
            }
        }

        if(score >= threshold){
            return "FRAUDULENT";
        }

        if(score >= 30){
            return "SUSPICIOUS";
        }

        return "NORMAL";
    }
}
