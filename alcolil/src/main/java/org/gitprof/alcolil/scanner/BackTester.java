package org.gitprof.alcolil.scanner;

import java.lang.Thread;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.scanner.CoreScanner;

public class BackTester {
	
	CoreScanner coreScanner;
	Thread scannerThread; 
	
	public void setConfs() {
		
	}
	
	public void backTest(AStockSeries stocks, AInterval interval, ATime from, ATime to) {
		coreScanner = new CoreScanner(CoreScanner.ScannerMode.BACKTEST, stocks, interval, from, to);
		scannerThread = new Thread(coreScanner);
		scannerThread.start();
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
