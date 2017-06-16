package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.global.Conf;

public class AlphaAnalyzer extends BaseAnalyzer {

    
	public AlphaAnalyzer(ATimeSeries timeSeries) {
		initialize(timeSeries);
	}
	
	@Override
	public void initialize(ATimeSeries timeSeries) {
		AlphaGraphAnalyzer oneMinAnalyzer = new AlphaGraphAnalyzer(timeSeries.getBarSeries(AInterval.ONE_MIN));
		// super.setGraphAnalyzer(oneMinAnalyzer);
		//AlphaGraphAnalyzer fiveMinAnalyzer = new AlphaGraphAnalyzer();
		//super.setGraphAnalyzer(fiveMinAnalyzer);
	}

	@Override
	public void updateNextQuote(AQuote quote) {
		super.updateNextQuote(quote);
		updateOneMinGraph(quote);
		updateFiveMinGraph(quote);
		demoAnalyzing(quote);
	}
	
	private void updateOneMinGraph(AQuote quote) {
		//BaseGraphAnalyzer graphAnalyzer = graphs.get(AInterval.ONE_MIN);
	}
	
	private void updateFiveMinGraph(AQuote quote) {

	}
	
	private void demoAnalyzing(AQuote quote) {
		if (50.0 < quote.high().floatValue()) {
			alert(quote);
		}
	}
	
	private void alert(AQuote quote) {
		alertingSystem.alert(quote);
	}
	
	private void loadConfigurations() {
	    
	}
}
