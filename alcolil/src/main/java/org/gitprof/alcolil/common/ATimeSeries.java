package org.gitprof.alcolil.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.math.BigDecimal;

/*
 * naming convention:
 * BarSeries - List<Quote>, all quotes of some interval for the specific symbol (e.g. all 1 min bars of symbol)
 * TimeSeries - all BarSeries of the specific symbol
 * 
 * ATimeSeries save and updates all the barSeries related to the symbol
 * 1 min bar update will cause updating all the larger interval barSeries. 
 * we assume the user adds bar in sequentially order one by one. if there are holes, will fill it with 'dead quotes'
 * in any other case - an error will raised.
 */
public class ATimeSeries {
	
	Map<AInterval, ABarSeries> graphs;
	String symbol;

	static AInterval[] supportedIntervals = {AInterval.ONE_MIN, 
													   AInterval.FIVE_MIN,
													   AInterval.DAILY};
	static int fiveMinCount = 0;
	static AQuote last5minQuote;
	
	public ATimeSeries(String symbol) {
		this.symbol = symbol;
		this.graphs = new HashMap<AInterval, ABarSeries>();
		initializeMapping();
	}
	
	public void initializeMapping() {
		graphs.put(AInterval.ONE_MIN, new ABarSeries(AInterval.ONE_MIN));
		graphs.put(AInterval.FIVE_MIN, new ABarSeries(AInterval.FIVE_MIN));
		graphs.put(AInterval.DAILY, new ABarSeries(AInterval.DAILY));
	}
	
	public AQuote getNext(AInterval interval) {
		//return graphs.get(interval).quoteIterator(ix);
		return null;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void addBarSeries(AInterval interval, ABarSeries series) {
		graphs.put(interval, series);
	}

	
	public Map<AInterval, ABarSeries> getQuoteMap() {
		return graphs;
	}
	
	public ABarSeries getBarSeries(AInterval interval) {
		return graphs.get(interval);
	}
	
	public void addQuote(AQuote quote) {
		graphs.get(quote.interval()).addQuote(quote);
	}
	
	//TODO: handle dead quotes
	public void add1minQuote(AQuote quote) {
		addQuote(quote);
		update5minQuote(quote);
	}
	
	//TODO: handle dead quotes
	public void update5minQuote(AQuote oneMinQuote) {
		if (0 == fiveMinCount) {
			last5minQuote = new AQuote(oneMinQuote);
		} else {
			last5minQuote.high(last5minQuote.high().max(oneMinQuote.high()));
			last5minQuote.low(last5minQuote.high().max(oneMinQuote.high()));
		}
		if (4 == fiveMinCount) {
			last5minQuote.close(oneMinQuote.close());
		}
		fiveMinCount = (++fiveMinCount % 5);
	}

	public static ATimeSeries mergeTimeSeries(ATimeSeries timeSeries1, ATimeSeries timeSeries2) {
		ATimeSeries newTimeSeries = new ATimeSeries(timeSeries1.getSymbol());
		for (AInterval interval : ATimeSeries.supportedIntervals) {
			ABarSeries newBarSeries = ABarSeries.mergeBarSeries(timeSeries1.getBarSeries(interval),
									                            timeSeries1.getBarSeries(interval));
			newTimeSeries.addBarSeries(interval, newBarSeries);
		}
		return newTimeSeries;
	}
}
