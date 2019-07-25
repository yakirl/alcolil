package org.yakirl.alcolil.scanner;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.database.DBManagerAPI;
import org.yakirl.alcolil.marketdata.FetcherAPI;
import org.yakirl.alcolil.scanner.BaseQuotePipe;
import org.yakirl.alcolil.strategy.BaseAnalyzer;

/*
 * Scanner operation:
 * *) scan collection of stocks, for one given interval graph, in a session
 * *) no option to reset scanner. to start a new scan - destroy and reconstruct 
 */
public class CoreScanner {
	
	protected static final Logger LOG = LogManager.getLogger(CoreScanner.class);
	
	private DBManagerAPI dbManager;
	private FetcherAPI fetcher;
	//private Map<String, BaseAnalyzer> analyzers;
	private Map<String, QuoteObserver> observers;
	private Thread quotePipeThread;
	private BaseQuotePipe quotePipe = null;
	private AtomicBoolean stop;
	private Object sleeper;
	private boolean debug;
	private int WAIT_FOR_PIPE_TIMEOUT_MILLIS = 10000;
		
	public CoreScanner(DBManagerAPI dbManager, FetcherAPI fetcher) {	
	    this.fetcher = fetcher;
		this.dbManager = dbManager;		
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
	private void setQuotePipe(ScannerMode mode, List<String> symbols, Interval interval, Time from, Time to) throws Exception {
		if (ScannerMode.REALTIME == mode) {
			quotePipe = new RealTimePipe(fetcher, symbols, from);
		} else { // BACKTEST
			quotePipe = new BackTestPipe(dbManager, fetcher, BackTestPipe.PipeSource.LOCAL, symbols, interval,  from, to);
		}
		quotePipeThread = new Thread(quotePipe);
		quotePipeThread.start(); // return immediately
	}
	
	public synchronized void stop() {
		sleeper.notify();
		stop.set(true);
	}
	
	public synchronized void waitForStop() {
		if (debug) {
			try {
				sleeper.wait();
			} catch(InterruptedException e) {}
		}
	}
	
	public void backtest(List<String> symbols, Interval interval, Time from, Time to, boolean debug, Map<String, QuoteObserver> observers) {
		try {
			LOG.info("Starting backtest. debug=" + String.valueOf(debug));
			setQuotePipe(ScannerMode.BACKTEST, symbols, interval, from, to);
			this.observers = observers != null ? observers : new HashMap<String, QuoteObserver>();
		} catch (Exception e) {
			LOG.error("failed to initialize backtest mode", e);
			return;
		}
		mainLoop();
	}
	
	public void realtime(List<String> symbols, Interval interval) {
		try {
			setQuotePipe(ScannerMode.REALTIME, symbols, interval, null, null);
		} catch (Exception e) {
			//TODO
		}
		mainLoop();
	}
	
	
	private void mainLoop() {
		Quote quote = null;
		//BaseAnalyzer analyzer;
		QuoteObserver obs;
		while(true) {
			try {
				quote = quotePipe.getNextQuote();
			} catch (Exception e) {
				LOG.error("mainLoop: failed to retrieve next quote from pipe", e);
				break;
			}
			if (quote.eof()) {
				waitForStop();
				break;
			}
			//analyzer = analyzers.get(quote.symbol());
			//analyzer.updateNextQuote(quote);
			obs = observers.get(quote.symbol());
			if (obs != null) {
				obs.observe(quote);
			}			
			if (stop.get()) {
				break;
			}
		}
		close();
	}

	private void close() {
		try {
			quotePipe.closePipe();
			quotePipeThread.join(WAIT_FOR_PIPE_TIMEOUT_MILLIS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
