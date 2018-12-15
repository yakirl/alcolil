package org.gitprof.alcolil.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class AStockCollection implements Iterable<AStock> {
 
	private Map<String, AStock> stocks;
	
	public AStockCollection() {
		stocks = new HashMap<String, AStock>();
	}
	
	public List<String> getSymbols() {
	    return new ArrayList<String>(stocks.keySet());		
	}
	
	public Iterator<AStock> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public AStock getStock(String symbol) throws Exception {
	    AStock ret = stocks.get(symbol);
	    if (ret == null) {
	    	throw new Exception("symbol " + symbol + " not found");
	    }
	    return ret;
	}
	
	public void add(AStock stock) {
		stocks.put(stock.getSymbol(), stock);
	}

}
