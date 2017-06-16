package org.gitprof.alcolil.strategy;

public class RuleFactory {

    public enum RuleType {
        RULE_DOUBLE_TOP_PRECISION,
        RULE_ROUND_PRICES
    }
    
    public Rule buildRule(RuleType ruleType, RuleParams ruleParams) {
        return new Rule(ruleType);
    }
    
    public class RuleParams {
        
    }
    
}
