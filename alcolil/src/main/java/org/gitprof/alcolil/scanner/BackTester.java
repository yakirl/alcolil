package org.gitprof.alcolil.scanner;

import java.util.List;

import java.lang.Thread;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.scanner.CoreScanner;

public class BackTester {
	
	CoreScanner coreScanner;
	Thread scannerThread; 
	
	public void setConfs() {
		
	}
	
	public void backtest(List<String> symbols, Interval interval, Time from, Time to) {
		//coreScanner = new CoreScanner(CoreScanner.ScannerMode.BACKTEST, symbols, interval, from, to);
		//coreScanner.backtest();
	}

	/* used only when running backtester in delayed mode for watching realtime progress
	 * this runs backtester as a thread */
	public void watchBacktest() {
		
	}

	public void stop() {
		coreScanner.stop();
		try {
			scannerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
