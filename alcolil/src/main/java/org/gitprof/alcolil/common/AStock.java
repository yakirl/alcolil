package org.gitprof.alcolil.common;

public class AStock {
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
}
