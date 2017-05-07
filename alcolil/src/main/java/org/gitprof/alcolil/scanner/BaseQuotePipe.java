package org.gitprof.alcolil.scanner;

import java.util.concurrent.atomic.AtomicBoolean;

import org.gitprof.alcolil.common.AQuote;
import org.gitprof.alcolil.marketdata.BaseFetcher;
import org.gitprof.alcolil.marketdata.QuoteQueue;

/*
 * The quote Pipe is aim to give a clean interface for marketData receiving, taking some of the logic from the Fetchers.
 * and enabling tracking of multi stocks simulatenouly
 * 
 * Both real time pipe and backtest pipe offer the same interface for pulling quotes:
 * the user construct the pipe with given stock collection to track, given interval, start time & end time. 
 * 	# note: -for realtime pipe we usually give only start point. in order to stop, the scanner will interrupt the pipe upon user request
 *  #             or in case the fetcher stop obtaining data (e.g. market closed)
 *  #       -for backtest - if no interval is given, than the pipe will pull the maximum available data from the fetcher
 *  # note2: - realtime pipe supports only 1min quotes
 *  #        - for backtest, the user can give GraphInterval: 1min or daily. 
 * then the user execute the pipe as a thread.
 * after that all the user has to do is using getNextQuote() for the quote streaming. 
 *  
 * the real time pipe pulls the data directly from remote server (marketdata fetcher)
 * the backtest pipe takes the data from local DB (thus user should update historical data in local DB using HistDataUpdater, before use)
 *   
 */

public abstract class BaseQuotePipe implements Runnable {

	BaseFetcher fetcher;
	QuoteQueue quoteQueue;
	AtomicBoolean closePipe;
	
	public void setMarketDataFetcher() {
		fetcher = BaseFetcher.getDefaultFetcher();
	}
	
	public BaseQuotePipe() {
		closePipe.set(false);
		setMarketDataFetcher();
	}
	
	public void closePipe() {
		closePipe.set(true);
	}
	
	public abstract void run();
	
	public void quoteQueuePush(AQuote quote) {
		quoteQueue.push(quote);
	}
	
	/*
	 * this method is used for getting any next quote - daily/intraday, history/realtime
	 */
	public AQuote getNextQuote() {
		AQuote nextQuote = quoteQueue.pop();
		return nextQuote;
	}
}
