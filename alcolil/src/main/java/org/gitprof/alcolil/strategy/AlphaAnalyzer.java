package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.global.Conf;

public class AlphaAnalyzer extends BaseAnalyzer {

    
	public AlphaAnalyzer(TimeSeries timeSeries) {
		initialize(timeSeries);
	}
	
	@Override
	public void initialize(TimeSeries timeSeries) {
		AlphaGraphAnalyzer oneMinAnalyzer = new AlphaGraphAnalyzer(timeSeries.getBarSeries(Interval.ONE_MIN));
		// super.setGraphAnalyzer(oneMinAnalyzer);
		//AlphaGraphAnalyzer fiveMinAnalyzer = new AlphaGraphAnalyzer();
		//super.setGraphAnalyzer(fiveMinAnalyzer);
	}

	@Override
	public void updateNextQuote(Quote quote) {
		super.updateNextQuote(quote);
		updateOneMinGraph(quote);
		updateFiveMinGraph(quote);
		demoAnalyzing(quote);
	}
	
	private void updateOneMinGraph(Quote quote) {
		//BaseGraphAnalyzer graphAnalyzer = graphs.get(AInterval.ONE_MIN);
	}
	
	private void updateFiveMinGraph(Quote quote) {

	}
	
	private void demoAnalyzing(Quote quote) {
		if (50.0 < quote.high().floatValue()) {
			alert(quote);
		}
	}
	
	private void alert(Quote quote) {
		alertingSystem.alert(quote);
	}
	
	private void loadConfigurations() {
	    
	}
}
