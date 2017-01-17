package org.gitprof.alcolil.common;

public class AStock {
	String symbol;
	String Sector;
	long marketCap;
	APrice lastPrice;
	long avgVolOfXDays;
	
	public long getMarketCap() {
		return marketCap;
	}
	
	public APrice getLastPrice() {
		return lastPrice;
	}
}
