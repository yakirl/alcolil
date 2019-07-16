package org.gitprof.alcolil.strategy;

import java.util.Vector;

import org.gitprof.alcolil.account.AlertingSystem;
import org.gitprof.alcolil.common.*;

public abstract class BaseAnalyzer {

	String symbol;	
	QuoteDataProcessor keeper;
	AlertingSystem alertingSystem;
	Vector<TriggerObserver> triggerObservers;
	
	public abstract void initialize(TimeSeries timeSeries);	
	
	public void updateNextQuote(Quote quote) {
		keeper.addQuote(quote);
		analyze();
	}
	
	public void registerTriggerObserver(TriggerObserver observer) {
	    triggerObservers.add(observer);
	}
	
	public void trigger() {
	    for (TriggerObserver observer : triggerObservers) {
	        observer.trigger();
	    }
	}
	
	public void loadStrategy(Strategy strategy) {
	    
	}
	
	private void analyze() {
	    
	}
}
