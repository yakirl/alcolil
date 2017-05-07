package org.gitprof.alcolil.strategy;

import java.util.Map;

import org.gitprof.alcolil.account.AlertingSystem;
import org.gitprof.alcolil.common.*;

public abstract class BaseAnalyzer {

	String symbol;
	Map<AInterval, BaseGraphAnalyzer> graphs;
	ATimeSeries timeSeries;
	AlertingSystem alertingSystem;
	
	public abstract void initialize(ATimeSeries timeSeries);
	
	public void setGraphAnalyzer(BaseGraphAnalyzer graphAnalyzer) {
		graphs.put(graphAnalyzer.getInterval(), graphAnalyzer);
	}
	
	public void updateNextQuote(AQuote quote) {
		timeSeries.addQuote(quote);
	}
}
