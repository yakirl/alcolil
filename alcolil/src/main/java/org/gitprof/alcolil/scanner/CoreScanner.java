package org.gitprof.alcolil.scanner;

import org.gitprof.alcolil.common.AQuote;
import org.gitprof.alcolil.scanner.BaseQuotePipe;

public class CoreScanner {

	BaseQuotePipe quotePipe = null;
	
	public enum ScannerMode {
		REALTIME, BACKTEST
	}
	
	private void setQuotePipe(ScannerMode mode) {
		if (ScannerMode.REALTIME == mode) {
			quotePipe = new RealTimePipe();
		} else { // BACKTEST
			quotePipe = new BackTestPipe();
		}
	}
	
	public void scan(ScannerMode mode) {
		setQuotePipe(mode);
		mainLoop();
	}
	
	private void mainLoop() {
		AQuote quote = quotePipe.getNextQuote();
		
	}
}
