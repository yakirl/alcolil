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
	QuoteStreamGather quoteStreamGather;
	Thread quoteStreamGatherThread = null;
	
	public YahooFetcher( ) {
		//super(quoteQueue);
	}
	
	@Override
	public void connect() {
		
	}

	@Override
	public void disconnect() {
		
	}
	
	@Override
	public void activateStreaming(QuoteQueue quoteQueue) {
		quoteStreamGather = new QuoteStreamGather(quoteQueue);
		quoteStreamGatherThread = new Thread(quoteStreamGather);
		quoteStreamGatherThread.start();
	}

	@Override
	public void deactivateStreaming() {
		// quoteStreamGather.stop.set(true);
		try {
			quoteStreamGatherThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void postRealTimeJobLine(String symbol, AInterval AInterval, ATime from) {
		LOG.error("Yahoo fetcher does not implement real time data!");
	}
	
	public void postHistoricalDataJobLine(String symbol, AInterval AInterval, ATime from, ATime to) {
		ABarSeries barSeries =  getHistoricalData(symbol, AInterval, from, null);
		// quoteStreamingHandler.addJob(barSeries);
	}
	
	@Override
	public ABarSeries getHistoricalData(String symbol, AInterval interval, ATime from, ATime to) {
		if (interval == AInterval.FIVE_MIN ||
			interval == AInterval.ONE_MIN)
			return getHistoricalIntraDay(symbol, interval, from, to);
		else
			return getHistoricalAboveDaily(symbol, interval, from, to);
	}
	
	private ABarSeries getHistoricalIntraDay(String symbol, AInterval interval, ATime from, ATime to) {
		return null;
	}
	
	//returns daily quotes
	private ABarSeries getHistoricalAboveDaily(String symbol, AInterval interval, ATime from, ATime to) {
		Stock yahooStock;
		ABarSeries barSeries = new ABarSeries(interval);
		try {
			yahooStock = YahooFinance.get(symbol, true);
			Interval yahooInterval = convertToYahooInterval(interval);
			List<HistoricalQuote> histQuotes = yahooStock.getHistory(yahooInterval);
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
