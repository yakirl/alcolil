package org.gitprof.alcolil.common;

import java.util.Map;

public class AStockSeries {

	private Map<String, ATimeSeries> mapping;
	
	public void add1minQuote(String symbol, AQuote quote) {
		mapping.get(symbol).add1minQuote(quote);
	}
}
