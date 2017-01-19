package org.gitprof.alcolil.marketdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.common.Enums.GraphInterval;
//import org.gitprof.alcolil.scanner.QuoteQueue;

public class YahooFetcher extends BaseFetcher {

	HistoricalDataHandler historicalDataHandler;
	//private Map<Integer, ABarSeries> jobs;
	//private Set<Integer> jobIds;

	
	
	public YahooFetcher( ) {
		//super(quoteQueue);
	}
	
	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	

	private class RealTimeDatahandler implements Runnable {

		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	// holds list of jobs (requests lines)
	// everytime pick one line, and push to QuoteQueue the next quote in from line.
	private class HistoricalDataHandler implements Runnable {
		
		public AtomicBoolean stop;
		private QuoteQueue quoteQueue;
		private List<ABarSeries> jobs;
		private Map<ABarSeries, Iterator<AQuote>> iterators;
		
		public HistoricalDataHandler(QuoteQueue quoteQueue) {
			stop = new AtomicBoolean();
			jobs = new ArrayList<ABarSeries>();
			iterators = new HashMap<ABarSeries, Iterator<AQuote>>();
			stop.set(false);
			this.quoteQueue = quoteQueue;
		}
		
		private void removeJob(ABarSeries barSeries) {
			accessJobs(barSeries, false);
		}
		
		private void addJob(ABarSeries barSeries) {
			accessJobs(barSeries, true);
		}
		
		private synchronized void accessJobs(ABarSeries barSeries, boolean addRemove) {
			
			//TODO: add job to jobs, and initialize iterator in iterators
		}
		
		private void executeJob(AQuote quote) {
			if (null == quote) {
				return;
			}
			quoteQueue.push(quote);	
		}
		
		public void jobExecutionLoop() {
			ABarSeries barSeries;
			AQuote nextQuote;
			while(stop.get()) {
				barSeries = jobs.get(new Random().nextInt(jobs.size()));
				nextQuote = iterators.get(barSeries).next();
				if (null == nextQuote) {
					removeJob(barSeries);
				}
				executeJob(nextQuote);
			}
		}

		@Override
		public void run() {
			jobExecutionLoop();
		}
	}
	
	@Override
	public void activateStreaming(QuoteQueue quoteQueue) {
		historicalDataHandler = new HistoricalDataHandler(quoteQueue);
		new Thread(historicalDataHandler).start();
	}

	@Override
	public void deactivateStreaming() {
		historicalDataHandler.stop.set(true);
	}
	
	@Override
	public void fetchRealTimeData(String symbol, Enums.GraphInterval graphInterval, ATime from) {
		ABarSeries barSeries =  getHistoricalData(symbol, graphInterval, from, null);
		historicalDataHandler.addJob(barSeries);
	}
	
	@Override
	public void fetchHistoricalData(String symbol, Enums.GraphInterval graphInterval, ATime from, ATime to) {
		
	}
	
	@Override
	public ABarSeries getHistoricalData(String symbol, Enums.GraphInterval graphInterval, ATime from, ATime to) {
		if (graphInterval == GraphInterval.FIVE_MIN ||
				graphInterval == GraphInterval.ONE_MIN)
			return getHistoricalIntraDay(symbol, graphInterval, from, to);
		else
			return getHistoricalAboveDaily(symbol, graphInterval, from, to);
	}
	
	private ABarSeries getHistoricalIntraDay(String symbol, Enums.GraphInterval graphInterval, ATime from, ATime to) {
		return null;
	}
	
	//returns daily quotes
	private ABarSeries getHistoricalAboveDaily(String symbol, Enums.GraphInterval graphInterval, ATime from, ATime to) {
		Stock yahooStock;
		ABarSeries barSeries = new ABarSeries();
		try {
			yahooStock = YahooFinance.get(symbol, true);
			Interval interval = convertToYahooInterval(graphInterval);
			List<HistoricalQuote> histQuotes = yahooStock.getHistory(interval);
			for (HistoricalQuote yahooQuote : histQuotes) {
				barSeries.addQuote(convertFromYahooQuote(yahooQuote));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return barSeries;
	}

	private HistoricalQuote convertToYahooQuote(AQuote quote) {
		return null;
	}
	
	private AQuote convertFromYahooQuote(HistoricalQuote yahooQuote) {
		AQuote quote = new AQuote(yahooQuote.getSymbol(),
				yahooQuote.getOpen(),
				yahooQuote.getHigh(),
				yahooQuote.getLow(),
				yahooQuote.getClose(),
				yahooQuote.getVolume().intValue(),
				Enums.GraphInterval.DAILY, // TODO: fix this shit
				convertFromYahooDate(yahooQuote.getDate()));
		return quote;								
	}
	
	private ATime convertFromYahooDate(Calendar date) {
		return null; // TODO
	}
	
	private Enums.GraphInterval convertFromYahooInterval(Interval yahooInterval) {
		if (yahooInterval == Interval.DAILY) {
			return Enums.GraphInterval.DAILY;
		} 
		return null;
	}
	
	private Interval convertToYahooInterval(Enums.GraphInterval interval) {
		if (interval == Enums.GraphInterval.DAILY) {
			return Interval.DAILY;
		} 
		return null;
	}
	
	/*
	public void getQuote() {
		
		Stock stock;
		try {
			stock = YahooFinance.get("INTC");
			BigDecimal price = stock.getQuote().getPrice();
			BigDecimal change = stock.getQuote().getChangeInPercent();
			BigDecimal peg = stock.getStats().getPeg();
			BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
				
			//stock.print();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
