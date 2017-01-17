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
	
	/*
	 * Running the QuotePipe.
	 * the QuotePipe responsible to fetch the quotes either from a marketData fetcher (Yahoo/IB as configured)
	 * or from DataBase, in case of backtest, or in case that we want real time scanning but starting the graph from a point in the past.
	 * if needed - the pipe will sync between old and new data
	 * the purpose is to give the scanner clean, neat interface for getting the next quote.
	 * The QuotePipe runs on its own thread.
	 * 
	 */
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
