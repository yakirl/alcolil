package org.gitprof.alcolil.common;

import java.math.BigDecimal;

import org.gitprof.alcolil.database.CSVable;;

public class AStock implements CSVable {
	private String symbol;
	private String sector;
	private BigDecimal marketCap;
	private BigDecimal lastPrice;
	private BigDecimal avgVolOfXDays;
	
	public BigDecimal getMarketCap() {
		return marketCap;
	}
	
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getSector() {
	    return sector;
	}

	@Override
	public String[] convertToCSV() {
	    String[] fields = new String[5];
        fields[0] = symbol;
        fields[1] = sector;
        fields[2] = String.valueOf(marketCap);
        fields[3] = lastPrice.toString();     
        fields[4] = String.valueOf(avgVolOfXDays);
		return fields;
	}

	@Override
	public CSVable initFromCSV(String[] csvs) {
		symbol = csvs[0];
		sector = csvs[1];
		marketCap = new BigDecimal(csvs[2]);
		lastPrice = new BigDecimal(csvs[3]);
		avgVolOfXDays = new BigDecimal(csvs[4]);
		return this;
	}
}
