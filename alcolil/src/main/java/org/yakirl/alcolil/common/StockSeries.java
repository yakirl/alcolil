package org.yakirl.alcolil.common;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class StockSeries {

	private Map<String, BarSeries> mapping;
	private Interval interval; 

	public Interval getInterval() {
		return interval;
	}
	
	public StockSeries(Interval interval) {
		this.interval = interval;
		mapping = new HashMap<String, BarSeries>();
	}

	public StockSeries(StockSeries stockSeries) {
		mapping = stockSeries.mapping = new HashMap<String, BarSeries>(stockSeries.mapping);
		interval = stockSeries.interval; // TODO: copy ctor for this
	}
	
	public void addQuote(String symbol, Quote quote) {
		mapping.get(symbol).addQuote(quote);
	}
	
	public void addBarSeries(String symbol, BarSeries barSeries) {
		mapping.put(symbol, barSeries);
	}
	
	public void removeBarSeries(String symbol) {
		mapping.remove(symbol);
	}
	
	public BarSeries getBarSeries(String symbol) {
		return mapping.get(symbol);	}
	
	public List<String> getSymbolList() {
		return new ArrayList<String>(mapping.keySet());
	}
	
	public int size() {
		return mapping.size();
	}
}
