package org.gitprof.alcolil.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;

public class ABarSeries {

	List<AQuote> quotes;
	Iterator<AQuote> quoteIterator;
	
	public ABarSeries() {
		quotes = new ArrayList<AQuote>();
	}
	
	public void addQuote(AQuote quote) {
		quotes.add(quote);
	}
	
	public int size() {
		return quotes.size();
	}
	
	public AQuote getQuote(int ix) {
		return quotes.get(ix);
	}
	
	public Iterator<AQuote> quoteIterator(int ix) {
		return quotes.listIterator(ix);
	}
}
