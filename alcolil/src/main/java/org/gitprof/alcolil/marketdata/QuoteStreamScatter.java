package org.gitprof.alcolil.marketdata;

import java.lang.Thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gitprof.alcolil.common.AStockSeries;
import org.gitprof.alcolil.common.ABarSeries;
import org.gitprof.alcolil.common.AQuote;

/*
 * holds list of jobs (requests lines) 
 * every iteration pick one line, and push to QuoteQueue the next quote in from line.
 *
 */

public class QuoteStreamScatter implements Runnable {
	
	public AtomicBoolean stop;
	private QuoteQueue quoteQueue;
	// TODO: currently we access to jobs via 2 threads - need to synchronize it or use HashTable
	private AStockSeries jobs;
	private Map<ABarSeries, Iterator<AQuote>> iterators;
	private int waitMillis;
	
	public QuoteStreamScatter(QuoteQueue quoteQueue, AStockSeries stockSeries) {
		stop = new AtomicBoolean();
		jobs = new AStockSeries(stockSeries);
		iterators = new HashMap<ABarSeries, Iterator<AQuote>>();
		stop.set(false);
		this.quoteQueue = quoteQueue;
		waitMillis = 100;
	}
	
	public void pushNewQuote(AQuote quote) {
		executeJob(quote);
	}
	
	private void removeEmptyJobLine(String symbol) {
		accessJobs(symbol, false);
	}
	
	// TODO: delete this method
	public void addJobLine(String symbol) {
		accessJobs(symbol, true);
	}
	
	private void accessJobs(String symbol, boolean addRemove) {
		jobs.removeBarSeries(symbol);
		//TODO: add job to jobs, and initialize iterator in iterators
	}
	
	private void executeJob(AQuote quote) {
		quoteQueue.push(quote);	
	}
	
	public void stop() {
		stop.set(true);
	}
	
	private void jobExecutionLoop() {
		String symbol;
		ABarSeries barSeries;
		AQuote nextQuote;
		while(stop.get() || (jobs.size() != 0)) {
			try {
				Thread.sleep(waitMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			symbol = jobs.getSymbolList().get(new Random().nextInt(jobs.size()));
			barSeries = jobs.getBarSeries(symbol);
			// nextQuote = iterators.get(barSeries).next();
			nextQuote = barSeries.nextQuote();
			if (null == nextQuote) {
				removeEmptyJobLine(symbol);
				continue;
			}
			executeJob(nextQuote);
		}
	}

	public void startStreaming() {
		jobExecutionLoop();
	}
	
	@Override
	public void run() {
		jobExecutionLoop();
	}
}
