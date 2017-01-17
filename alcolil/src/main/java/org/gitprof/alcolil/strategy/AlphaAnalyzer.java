package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.AQuote;
import org.gitprof.alcolil.common.Enums.GraphInterval;

public class AlphaAnalyzer extends BaseAnalyzer {

	@Override
	public void initialize() {
		AlphaGraphAnalyzer oneMinAnalyzer = new AlphaGraphAnalyzer();
		super.setGraphAnalyzer(oneMinAnalyzer);
		AlphaGraphAnalyzer fiveMinAnalyzer = new AlphaGraphAnalyzer();
		super.setGraphAnalyzer(fiveMinAnalyzer);
	}

	@Override
	public void updateNextQuote(AQuote quote) {
		updateOneMinGraph(quote);
		updateFiveMinGraph(quote);
		
	}
	
	public void updateOneMinGraph(AQuote quote) {
		BaseGraphAnalyzer graphAnalyzer = graphs.get(GraphInterval.ONE_MIN);
	}
	
	public void updateFiveMinGraph(AQuote quote) {

	}
}
