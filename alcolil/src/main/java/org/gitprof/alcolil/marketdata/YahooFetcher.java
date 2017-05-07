package org.gitprof.alcolil.marketdata;

import java.lang.Thread;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.common.AInterval;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import org.gitprof.alcolil.core.Core;

public class YahooFetcher extends BaseFetcher {

	protected static final Logger LOG = LogManager.getLogger(Core.class);
	QuoteStreamScatter quoteStreamingHandler;
	Thread quoteStreamingHandlerThread = null;
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
	
	@Override
	public void activateStreaming(QuoteQueue quoteQueue) {
		quoteStreamingHandler = new QuoteStreamScatter(quoteQueue);
		quoteStreamingHandlerThread = new Thread(quoteStreamingHandler);
		quoteStreamingHandlerThread.start();
	}

	@Override
	public void deactivateStreaming() {
		quoteStreamingHandler.stop.set(true);
		try {
			quoteStreamingHandlerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void postRealTimeJobLine(String symbol, AInterval graphInterval, ATime from) {
		LOG.error("Yahoo fetcher does not implement real time data!");
	}
	
	public void postHistoricalDataJobLine(String symbol, AInterval graphInterval, ATime from, ATime to) {
		ABarSeries barSeries =  getHistoricalData(symbol, graphInterval, from, null);
		quoteStreamingHandler.addJob(barSeries);
	}
	
	@Override
	public ABarSeries getHistoricalData(String symbol, AInterval graphInterval, ATime from, ATime to) {
		if (graphInterval == AInterval.FIVE_MIN ||
				graphInterval == AInterval.ONE_MIN)
			return getHistoricalIntraDay(symbol, graphInterval, from, to);
		else
			return getHistoricalAboveDaily(symbol, graphInterval, from, to);
	}
	
	private ABarSeries getHistoricalIntraDay(String symbol, AInterval graphInterval, ATime from, ATime to) {
		return null;
	}
	
	//returns daily quotes
	private ABarSeries getHistoricalAboveDaily(String symbol, AInterval graphInterval, ATime from, ATime to) {
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
	
	private AQuote convertFromYahooQuote(HistoricalQuote yahooQuote) {
		AQuote quote = new AQuote(yahooQuote.getSymbol(),
				yahooQuote.getOpen(),
				yahooQuote.getHigh(),
				yahooQuote.getLow(),
				yahooQuote.getClose(),
				yahooQuote.getVolume().intValue(),
				AInterval.DAILY, // TODO: fix this shit
				convertFromYahooDate(yahooQuote.getDate()));
		return quote;								
	}
	
	private ATime convertFromYahooDate(Calendar date) {
		return null; // TODO
	}
	
	private AInterval convertFromYahooInterval(Interval yahooInterval) {
		if (yahooInterval == Interval.DAILY) {
			return AInterval.DAILY;
		} 
		return null;
	}
	
	private Interval convertToYahooInterval(AInterval interval) {
		if (interval == AInterval.DAILY) {
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
