package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.*;

public class AlphaAnalyzer extends BaseAnalyzer {

	public AlphaAnalyzer(ATimeSeries timeSeries) {
		initialize(timeSeries);
	}
	
	@Override
	public void initialize(ATimeSeries timeSeries) {
		AlphaGraphAnalyzer oneMinAnalyzer = new AlphaGraphAnalyzer(timeSeries.getBarSeries(Enums.GraphInterval.ONE_MIN));
		super.setGraphAnalyzer(oneMinAnalyzer);
		//AlphaGraphAnalyzer fiveMinAnalyzer = new AlphaGraphAnalyzer();
		//super.setGraphAnalyzer(fiveMinAnalyzer);
	}

	@Override
	public void updateNextQuote(AQuote quote) {
		updateOneMinGraph(quote);
		updateFiveMinGraph(quote);
		
	}
	
	public void updateOneMinGraph(AQuote quote) {
		BaseGraphAnalyzer graphAnalyzer = graphs.get(Enums.GraphInterval.ONE_MIN);
	}
	
	public void updateFiveMinGraph(AQuote quote) {

	}
}
