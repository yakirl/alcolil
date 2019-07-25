package org.yakirl.alcolil.marketdata;

import java.lang.Thread;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yakirl.alcolil.common.BarSeries;
import org.yakirl.alcolil.common.Quote;
import org.yakirl.alcolil.common.StockSeries;
import org.yakirl.alcolil.core.Core;

/**************************
 *   QuoteStreamScatter
 *   
 *   The component is used mainly for backtest.
 *   It holds a list of barSeries and a QuoteQueue, and every iteration choose a barSeries, pop quote from it
 *   	and publish to QuoteQueue.
 *   
 *   Usage:
 * 		1. initialize with jobs, and QuoteQueue to publish
 *      2. call startStreamingAsync()
 *      3. listen and read from QuoteQueue
 *
 ***************************/

public class QuoteStreamScatter {
	
	protected static final Logger LOG = LogManager.getLogger(Core.class);
	public AtomicBoolean stop;
	private QuoteQueue quoteQueue;
	// TODO: currently we access to jobs via 2 threads - need to synchronize it or use HashTable
	private StockSeries jobs;
	private int waitMillis;
	private Thread t;
	
	public QuoteStreamScatter(QuoteQueue quoteQueue, StockSeries stockSeries) {
		stop = new AtomicBoolean();
		jobs = new StockSeries(stockSeries);
		// iterators = new HashMap<BarSeries, Iterator<Quote>>();
		stop.set(false);
		this.quoteQueue = quoteQueue;
		waitMillis = 100;
	}
	
	public void startStreaming() {
		jobExecutionLoop();		
	}
	
	public void stop() {
		stop.set(true);
	}
	
	private void removeEmptyJobLine(String symbol) {
		accessJobs(symbol, false);
	}
	
	private void accessJobs(String symbol, boolean addRemove) {
		jobs.removeBarSeries(symbol);
	}
	
	private void executeJob(Quote quote) {
		quoteQueue.push(quote);	
	}
	
	private void jobExecutionLoop() {
		String symbol;
		BarSeries barSeries;
		Quote nextQuote;
		while(!stop.get() && (jobs.size() != 0)) {
			try {
				Thread.sleep(waitMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				symbol = jobs.getSymbolList().get(new Random().nextInt(jobs.size()));
				barSeries = jobs.getBarSeries(symbol);
				nextQuote = barSeries.nextQuote();
				if (null == nextQuote) {
					removeEmptyJobLine(symbol);
					continue;
				}
				executeJob(nextQuote);
			} catch (Exception e) {
				LOG.error("Failed to post quote", e);
			}
		}
		executeJob((new Quote()).setEof());
	}
}
