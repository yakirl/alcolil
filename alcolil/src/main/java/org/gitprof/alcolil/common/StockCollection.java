package org.gitprof.alcolil.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class StockCollection implements Iterable<Stock> {
 
	private Map<String, Stock> stocks;
	
	public StockCollection() {
		stocks = new HashMap<String, Stock>();
	}
	
	public List<String> getSymbols() {
	    return new ArrayList<String>(stocks.keySet());		
	}
	
	public Iterator<Stock> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Stock getStock(String symbol) throws Exception {
	    Stock ret = stocks.get(symbol);
	    if (ret == null) {
	    	throw new Exception("symbol " + symbol + " not found");
	    }
	    return ret;
	}
	
	public void add(Stock stock) {
		stocks.put(stock.getSymbol(), stock);
	}

}
