package org.yakirl.alcolil.scanner;

import java.util.List;
import java.util.Map;

import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.database.DBManagerAPI;
import org.yakirl.alcolil.marketdata.FetcherAPI;
import org.yakirl.alcolil.scanner.CoreScanner;

import java.lang.Thread;

public class BackTester {
	
	CoreScanner coreScanner;
	// Thread scannerThread; 
	
	public BackTester(DBManagerAPI dbManager, FetcherAPI fetcher) {
		coreScanner = new CoreScanner(dbManager, fetcher);	
	}
	
	public void setConfs() {
		
	}
	
	
	public void backtest(List<String> symbols, Interval interval, Time from, Time to, boolean debug, Map<String, QuoteObserver> observers) {
		coreScanner.backtest(symbols, interval, from, to, debug, observers);
	}
	
	public void stop() {
		coreScanner.stop();
	}	
}
