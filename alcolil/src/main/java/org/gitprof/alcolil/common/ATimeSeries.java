package org.gitprof.alcolil.common;

import java.util.ArrayList;
import java.util.Map;

public class ATimeSeries {
	
	Map<Enums.GraphInterval, ArrayList<AQuote>> quotes;
	String symbol;
	
	public void addQuote(AQuote quote) {
		//quotes.add(quote);
	}
	
	public String getSymbol() {
		return symbol;
	}
}
