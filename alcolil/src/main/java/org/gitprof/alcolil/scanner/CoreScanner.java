package org.gitprof.alcolil.scanner;

import java.util.Map;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.scanner.BaseQuotePipe;
import org.gitprof.alcolil.strategy.BaseAnalyzer;

/*
 * Scanner operation:
 * *) scan collection of stocks, for one given interval graph, in a session
 * *) no option to reset scanner. to start a new scan - destroy and reconstruct 
 */
public class CoreScanner implements Runnable {

	private AStockSeries stocks;
	private Map<String, BaseAnalyzer> analyzers;
	private Thread quotePipeThread;
	private BaseQuotePipe quotePipe = null;
	private AInterval interval;
	private ATime start = null;
	private ATime stop = null;
	private ScannerMode mode;
	private int WAIT_FOR_PIPE_TIMEOUT_MILLIS = 10000;
	
	public CoreScanner(ScannerMode mode, AStockSeries stocks, AInterval interval, ATime from, ATime to) {
		this.stocks = stocks;
		this.interval = interval;
		this.mode = mode;
		this.start = from;
		this.stop = to;
		initializeAnalyzers();
	}
	
	public enum ScannerMode {
		REALTIME, BACKTEST
	}
	
	// we use only alpha analyzer for now
	private void initializeAnalyzers() {
		
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
	private void setQuotePipe(ScannerMode mode, AInterval interval, ATime from, ATime to) {
		if (ScannerMode.REALTIME == mode) {
			quotePipe = new RealTimePipe(stocks, from);
		} else { // BACKTEST
			quotePipe = new BackTestPipe(BackTestPipe.PipeSource.LOCAL, stocks, interval,  from, to);
		}
		quotePipeThread = new Thread(quotePipe);
		quotePipeThread.start(); // return immediately
		//quotePipe.startQuoteStreaming(); //return immediately
	}
	
	public void stop() {
		
	}
	
	public void backtest() {
		setQuotePipe(ScannerMode.BACKTEST, interval, start, stop);
		mainLoop();
	}
	
	public void realtime() {
		setQuotePipe(ScannerMode.REALTIME, interval, start, null);
		mainLoop();
	}
	
	
	private void mainLoop() {
		AQuote quote;
		BaseAnalyzer analyzer;
		while(true) {
			quote = quotePipe.getNextQuote();
			if (quote.eof())
				break;
			analyzer = analyzers.get(quote.symbol());
			analyzer.updateNextQuote(quote);
			if ((null != stop) && stop.before(quote.time())) {
				break;
			}
		}
		close();
	}

	private void close() {
		try {
			quotePipeThread.join(WAIT_FOR_PIPE_TIMEOUT_MILLIS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		if (ScannerMode.BACKTEST == mode) {
			backtest();
		} else { // REALTIME
			realtime();
		}
		
	}
}
