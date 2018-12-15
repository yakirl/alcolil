package org.gitprof.alcolil.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.math.BigDecimal;

/* 
 * TimeSeries save and updates all the barSeries related to the symbol
 * 1 min bar update will cause updating all the larger interval barSeries. 
 * we assume the user adds bar in sequentially order one by one. if there are holes, will fill it with 'dead quotes'
 * in any other case - an error will raised.
 */

public class TimeSeries {
	
	Map<Interval, BarSeries> graphs;
	String symbol;

	static Interval[] supportedIntervals = {Interval.ONE_MIN, 
											Interval.FIVE_MIN,
											Interval.DAILY};
	static int fiveMinCount = 0;
	static Quote last5minQuote;
	
	public TimeSeries(String symbol) {
		this.symbol = symbol;
		this.graphs = new HashMap<Interval, BarSeries>();
		initializeMapping(symbol);
	}
	
	public void initializeMapping(String symbol) {
		graphs.put(Interval.ONE_MIN, new BarSeries(symbol, Interval.ONE_MIN));
		graphs.put(Interval.FIVE_MIN, new BarSeries(symbol, Interval.FIVE_MIN));
		graphs.put(Interval.DAILY, new BarSeries(symbol, Interval.DAILY));
	}
	
	public Quote getNext(Interval interval) {
		//return graphs.get(interval).quoteIterator(ix);
		return null;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void addBarSeries(Interval interval, BarSeries series) {
		graphs.put(interval, series);
	}

	public Map<Interval, BarSeries> getQuoteMap() {
		return graphs;
	}
	
	public BarSeries getBarSeries(Interval interval) {
		return graphs.get(interval);
	}
	
	public void addQuote(Quote quote) {
		graphs.get(quote.interval()).addQuote(quote);
	}
	
	//TODO: handle dead quotes
	public void add1minQuote(Quote quote) {
		addQuote(quote);
		update5minQuote(quote);
	}
	
	//TODO: handle dead quotes
	public void update5minQuote(Quote oneMinQuote) {
		if (0 == fiveMinCount) {
			last5minQuote = new Quote(oneMinQuote);
		} else {
			last5minQuote.high(last5minQuote.high().max(oneMinQuote.high()));
			last5minQuote.low(last5minQuote.high().max(oneMinQuote.high()));
		}
		if (4 == fiveMinCount) {
			last5minQuote.close(oneMinQuote.close());
		}
		fiveMinCount = (++fiveMinCount % 5);
	}

	public static TimeSeries mergeTimeSeries(TimeSeries timeSeries1, TimeSeries timeSeries2) {
		TimeSeries newTimeSeries = new TimeSeries(timeSeries1.getSymbol());
		for (Interval interval : TimeSeries.supportedIntervals) {
			BarSeries newBarSeries = BarSeries.mergeBarSeries(timeSeries1.getBarSeries(interval),
									                            timeSeries1.getBarSeries(interval));
			newTimeSeries.addBarSeries(interval, newBarSeries);
		}
		return newTimeSeries;
	}
}
