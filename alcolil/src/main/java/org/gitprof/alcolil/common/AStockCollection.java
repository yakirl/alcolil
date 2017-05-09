package org.gitprof.alcolil.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class AStockCollection implements Iterable<AStock> {
 
	private ArrayList<AStock> stocks;
	
	public AStockCollection() {
		stocks = new ArrayList<AStock>();
	}
	
	public List<String> getSymbols() {
		String[] symbols = new String[stocks.size()];
		int i = 0;
		for (AStock stock : stocks) {
			symbols[i] = stock.getSymbol();
			++i;
		}
		return new ArrayList<String>(Arrays.asList(symbols));
	}
	
	public Iterator<AStock> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void add(AStock stock) {
		stocks.add(stock);
	}

}
