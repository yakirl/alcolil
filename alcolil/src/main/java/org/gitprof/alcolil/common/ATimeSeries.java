package org.gitprof.alcolil.common;

import java.util.ArrayList;

public class ATimeSeries {
	ArrayList<AQuote> quotes;
	
	public void addQuote(AQuote quote) {
		quotes.add(quote);
	}
}
