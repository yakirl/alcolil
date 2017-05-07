package org.gitprof.alcolil.common;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class AStockSeries {

	private Map<String, ABarSeries> mapping;
	private AInterval interval; 

	public AInterval getInterval() {
		return interval;
	}
	
	public AStockSeries(AInterval interval) {
		this.interval = interval;
		mapping = new HashMap<String, ABarSeries>();
	}

	public AStockSeries(AStockSeries stockSeries) {
		mapping = stockSeries.mapping = new HashMap<String, ABarSeries>(stockSeries.mapping);
		interval = stockSeries.interval; // TODO: copy ctor for this
	}
	
	public void addQuote(String symbol, AQuote quote) {
		mapping.get(symbol).addQuote(quote);
	}
	
	public void addBarSeries(String symbol, ABarSeries barSeries) {
		mapping.put(symbol, barSeries);
	}
	
	public void removeBarSeries(String symbol) {
		mapping.remove(symbol);
	}
	
	public ABarSeries getBarSeries(String symbol) {
		return mapping.get(symbol);	}
	
	public List<String> getSymbolList() {
		return new ArrayList<String>(mapping.keySet());
	}
	
	public int size() {
		return mapping.size();
	}
}
