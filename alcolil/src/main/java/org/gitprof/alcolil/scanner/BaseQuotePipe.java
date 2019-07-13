package org.gitprof.alcolil.scanner;

import java.util.concurrent.atomic.AtomicBoolean;

import org.gitprof.alcolil.common.Quote;
import org.gitprof.alcolil.marketdata.FetcherAPI;
import org.gitprof.alcolil.marketdata.QuoteQueue;
import org.gitprof.alcolil.marketdata.YahooFetcher;

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
 *  #        - for backtest, the user can give AInterval: 1min or daily. 
 * then the user execute the pipe as a thread.
 * after that all the user has to do is using getNextQuote() for the quote streaming. 
 *  
 * the real time pipe pulls the data directly from remote server (marketdata fetcher)
 * the backtest pipe takes the data from local DB (thus user should update historical data in local DB using HistDataUpdater, before use)
 *   
 */

public abstract class BaseQuotePipe implements Runnable {

	FetcherAPI fetcher;
	QuoteQueue quoteQueue;
	
	public BaseQuotePipe(FetcherAPI fetcher) {
		this.fetcher = fetcher;	  
	}
	
	public abstract void closePipe();
	
	public abstract void run();
		
	/*
	 * this method is used for getting any next quote - daily/intraday, history/realtime
	 * Always return a non-null quote, or throws exception
	 */
	public Quote getNextQuote() throws Exception {
		Quote nextQuote = quoteQueue.pop();
		return nextQuote;
	}
}
