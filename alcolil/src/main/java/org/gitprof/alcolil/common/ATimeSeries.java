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
	
	Map<Enums.GraphInterval, ABarSeries> graphs;
	String symbol;

	static Enums.GraphInterval[] supportedIntervals = {Enums.GraphInterval.ONE_MIN, 
													   Enums.GraphInterval.FIVE_MIN,
													   Enums.GraphInterval.DAILY};
	static int fiveMinCount = 0;
	static AQuote last5minQuote;
	
	public ATimeSeries(String symbol) {
		this.symbol = symbol;
		this.quotes = new HashMap<Enums.GraphInterval, List<AQuote>>();
		initializeMapping();
	}
	
	public void initializeMapping() {
		graphs.put(Enums.GraphInterval.ONE_MIN, new ABarSeries());
		graphs.put(Enums.GraphInterval.FIVE_MIN, new ABarSeries());
		graphs.put(Enums.GraphInterval.DAILY, new ABarSeries());
	}
	
	public AQuote getNext(Enums.GraphInterval interval) {
		return quoteIterators.get(interval).next();
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void addBarSeries(Enums.GraphInterval interval, List<AQuote> series) {
		graphs.put(interval, series);
	}

	
	public Map<Enums.GraphInterval, ABarSeries> getQuoteMap() {
		return graphs;
	}
	
	public ABarSeries getBarSeries(Enums.GraphInterval interval) {
		return graphs.get(interval);
	}
	
	public void addQuote(AQuote quote) {
		//quotes.add(quote);
	}
	
	//TODO: handle dead quotes
	public void add1minQuote(AQuote quote) {
		graphs.get(quote.interval()).add(quote);
		update5minQuote(quote);
	}
	
	//TODO: handle dead quotes
	public void update5minQuote(AQuote oneMinQuote) {
		if (0 == fiveMinCount) {
			last5minQuote = new AQuote(oneMinQuote);
		} else {
			last5minQuote.high(BigDecimal.max(last5minQuote.high(), oneMinQuote.high()));
			last5minQuote.low(BigDecimal.max(last5minQuote.high(), oneMinQuote.high()));
		}
		if (4 == fiveMinCount) {
			last5minQuote.close(oneMinQuote.close());
		}
		fiveMinCount = (++fiveMinCount % 5);
	}
	
	public void addDailyQuote(AQuote quote) {
		
	}
	
	public static ATimeSeries mergeTimeSeries(ATimeSeries timeSeries1, ATimeSeries timeSeries2) {
		ATimeSeries newTimeSeries = new ATimeSeries(timeSeries1.getSymbol());
		for (Enums.GraphInterval interval : ATimeSeries.supportedIntervals) {
			List<AQuote> newBarSeries = ATimeSeries.mergeBarSeries(timeSeries1.getBarSeries(interval),
									                               timeSeries1.getBarSeries(interval));
			newTimeSeries.addBarSeries(interval, newBarSeries);
		}
		return newTimeSeries;
	}
	
	/*
	 * support 1 minute only !
	 * Merge 2 barSeries (of the same bar interval)
	 * if there is a hole between them - fill with dead quotes
	 */
	public static List<AQuote> mergeBarSeries(List<AQuote> barSeries1, List<AQuote> barSeries2) {
		//TODO: handle empty lists
		//if (barSeries1.isEmpty())
		//	ArrayList<AQuote> abc = (ArrayList<AQuote>) barSeries2.clone();
		ATime start1 = barSeries1.get(0).time();
		ATime start2 = barSeries2.get(0).time();
		ATime end1   = barSeries1.get(barSeries1.size()-1).time();
		ATime end2   = barSeries2.get(barSeries2.size()-1).time();
		List<AQuote> first, second, newBarSeries = new ArrayList<AQuote>();
		first  = start1.before(start2) ? barSeries1 : barSeries2;
		second = start1.before(start2) ? barSeries2 : barSeries1;
		
		ATime startSecond = start1.before(start2) ? start2 : start1;
		ATime endSecond   = start1.before(start2) ? end2   :   end1;
		ATime endFirst    = start1.before(start2) ? end1   :   end2;
		
		for (AQuote quote : first) {
			newBarSeries.add(quote);
		}
		ATime nextTime = ATime.addMinute(endFirst);
		// if we havent gone to the end of the secondBarSeries:
		if (nextTime.before(endSecond)) {
			// fill hole with dead quotes
			while (nextTime.before(startSecond)) {
				//add dead quotes
				newBarSeries.add(new AQuote());
				nextTime = ATime.addMinute(nextTime);
			} 
			// fill the rest with the second series
			for (AQuote quote : second) {
				newBarSeries.add(quote);
			}
		}
		return newBarSeries;
	}
}
