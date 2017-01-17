package org.gitprof.alcolil.scanner;

import java.util.Map;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.scanner.BaseQuotePipe;
import org.gitprof.alcolil.strategy.BaseAnalyzer;

public class CoreScanner {

	AStockCollection stocks;
	Map<String, BaseAnalyzer> analyzers;
	Thread quotePipeThread;
	BaseQuotePipe quotePipe = null;
	ATime stop = null;
	
	public CoreScanner() {
		
	}
	
	private enum ScannerMode {
		REALTIME, BACKTEST
	}
	
	private void setQuotePipe(ScannerMode mode, ATime from, ATime to) {
		if (ScannerMode.REALTIME == mode) {
			quotePipe = new RealTimePipe(stocks, from);
		} else { // BACKTEST
			quotePipe = new BackTestPipe(stocks, from, to);
		}
		quotePipeThread = new Thread(quotePipe);
		quotePipeThread.start(); // return immediately
		//quotePipe.startQuoteStreaming(); //return immediately
	}
	
	public void backtest(AStockCollection stocks, ATime from, ATime to) {
		this.stocks = stocks;
		setQuotePipe(ScannerMode.BACKTEST, from, to);
		stop = to;
		mainLoop();
	}
	
	public void realtime(AStockCollection stocks, ATime from) {
		this.stocks = stocks;
		setQuotePipe(ScannerMode.REALTIME, from, null);
		mainLoop();
	}
	
	private void mainLoop() {
		AQuote quote;
		BaseAnalyzer analyzer;
		while(true) {
			quote = quotePipe.getNextQuote();
			analyzer = analyzers.get(quote.getSymbol());
			analyzer.updateNextQuote(quote);
		}
	}
}
