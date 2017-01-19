package org.gitprof.alcolil.strategy;

import java.util.Map;

import org.gitprof.alcolil.common.*;

public abstract class BaseAnalyzer {

	String symbol;
	Map<Enums.GraphInterval, BaseGraphAnalyzer> graphs;
	
	public abstract void initialize(ATimeSeries timeSeries);
	
	public void setGraphAnalyzer(BaseGraphAnalyzer graphAnalyzer) {
		graphs.put(graphAnalyzer.getInterval(), graphAnalyzer);
	}
	
	public abstract void updateNextQuote(AQuote quote);
}
