package org.gitprof.alcolil.scanner;

import java.util.List;
import java.util.Map;
import java.lang.Thread;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.scanner.CoreScanner;
import org.gitprof.alcolil.database.DBManagerAPI;
import org.gitprof.alcolil.marketdata.FetcherAPI;

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
		/*try {
			scannerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}	
}
