package org.gitprof.alcolil.common;

import java.math.BigDecimal;

import org.gitprof.alcolil.database.CSVable;;

public class AStock implements CSVable {
	private String symbol;
	private String Sector;
	private long marketCap;
	private BigDecimal lastPrice;
	private long avgVolOfXDays;
	
	public long getMarketCap() {
		return marketCap;
	}
	
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	
	public String getSymbol() {
		return symbol;
	}

	@Override
	public String[] convertToCSV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CSVable initFromCSV(String[] csvs) {
		// TODO Auto-generated method stub
		return null;
	}
}
