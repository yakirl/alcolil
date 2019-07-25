package org.yakirl.alcolil.strategy;

// import java.util.ArrayList;
import java.util.Vector;

import org.yakirl.alcolil.common.*;

import java.util.HashSet;

public class Rule {
    private Vector<GraphEvent> events;
    int expectedEventIX;
    private Vector<HashSet<GraphEvent>> resetEvents;
    private Quote lastQuote;
    
    public Rule(RuleFactory.RuleType ruleType) {
        expectedEventIX = 0;
        events = new Vector<GraphEvent>();
        resetEvents = new Vector<HashSet<GraphEvent>>();
        lastQuote = null;
    }

    public boolean isBingo() {
        return false;
    }
    
    private void bingo() {
        
    }
    
    public void updateRule(Quote quote) {
        lastQuote = quote;
        assert expectedEventIX < events.size() : "continue checking rule after bingo!";
        if (expectedEventIX > 0) {
            if (shouldReset()) {
                resetRule();
            }
        }
        GraphEvent expectedEvent = events.get(expectedEventIX);
        expectedEvent.updateQuote(lastQuote);
        if (expectedEvent.isCompleted()) {
            expectedEventIX++;
            if (expectedEventIX == events.size()) {
                bingo();
            }
        }        
        
    }
        
    private boolean shouldReset() {
        boolean toReset = false;
        for (GraphEvent event : resetEvents.get(expectedEventIX)) {
            event.updateQuote(lastQuote);
            if (event.isCompleted()) {
                toReset = true;                
                break;
            }
        }
        return toReset;
    }
    
    private void resetRule() {
    }
        
    
}
