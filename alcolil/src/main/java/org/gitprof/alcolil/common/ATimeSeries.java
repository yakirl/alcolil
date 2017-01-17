package org.gitprof.alcolil.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/*
 * naming convention:
 * BarSeries - List<Quote>, all quotes of some interval for the specific symbol (e.g. all 1 min bars of symbol)
 * TimeSeries - all BarSeries of the specific symbol
 */
public class ATimeSeries {
	
	Map<Enums.GraphInterval, List<AQuote>> quotes;
	String symbol;
	
	public void addQuote(AQuote quote) {
		//quotes.add(quote);
	}
	
	public void addSeries(Enums.GraphInterval interval, List<AQuote> series) {
		quotes.put(interval, series);
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public Map<Enums.GraphInterval, List<AQuote>> getQuoteMap() {
		return quotes;
	}
	
	public List<AQuote> getQuotesByInterval(Enums.GraphInterval interval) {
		return quotes.get(interval);
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
		ATime start1 = barSeries1.get(barSeries1.size()-1).getTime();
		ATime end1 = barSeries1.get(0).getTime();
		ATime start2 = barSeries1.get(barSeries2.size()-1).getTime();
		ATime end2 = barSeries2.get(0).getTime();
		List<AQuote> first, second, newBarSeries = new ArrayList<AQuote>();
		first  = start1.before(start2) ? barSeries1 : barSeries2;
		second = start1.before(start2) ? barSeries2 : barSeries1;
		for (AQuote quote : first) {
			newBarSeries.add(quote);
		}
		ATime nextTime = ATime.addMinute(newBarSeries.get(barSeries2.size()-1).getTime());
		// if we havent gone to the end of the secondBarSeries:
		if (nextTime.before(second.get(second.size()-1).getTime())) {
			// fill hole with dead quotes
			while (nextTime.before(second.get(0).getTime())) {
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
