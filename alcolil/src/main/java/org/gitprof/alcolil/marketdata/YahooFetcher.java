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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.*;

import org.apache.commons.lang3.ArrayUtils;
// import org.apache.lang.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gitprof.alcolil.common.*;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import org.gitprof.alcolil.core.Core;


public class YahooFetcher extends BaseFetcher {

    private int MAXIMUM_DAYS_FOR_HISTORICAL_QUTOES = 10; // this is totally arbitrary :-\
    private static String ONE_MIN_QUOTES_URL_PATTERN = "https://chartapi.finance.yahoo.com/instrument/1.0/TICKER/chartdata;type=quote;range=RANGE/csv";
	protected static final Logger LOG = LogManager.getLogger(Core.class);
	QuoteStreamGather quoteStreamGather;
	Thread quoteStreamGatherThread = null;
	
	public YahooFetcher( ) {

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
		try {
			quoteStreamGatherThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void postRealTimeJobLine(String symbol, AInterval AInterval, ATime from) {
		assert false : "Yahoo fetcher doesnt support real-time quotes!";
	}
	
	public void postHistoricalDataJobLine(String symbol, AInterval AInterval, ATime from, ATime to) {
		assert false : "Yahoo fetcher doesnt support async fetching of historical data!";		
	}
	
	@Override
	public ABarSeries getHistoricalData(String symbol, AInterval interval, ATime from, ATime to) {
	    assert (interval != null) && (symbol != null) : "getHistoricalDate: got nulls parameters!";	    
	    if (to == null)
	        to = ATime.now();
	    if (from == null)
            from = new ATime(to.getDateTime().minusDays(MAXIMUM_DAYS_FOR_HISTORICAL_QUTOES));
	    LOG.info(String.format("get historical data: symbol=%s. interval=%s. from=%s. to=%s",
	             symbol, interval.toString(), from.toString(), to.toString()));
		ABarSeries barSeries = null;
	    if (interval == AInterval.ONE_MIN) {
			barSeries =  getHistoricalIntraDay(symbol, interval, from, to);
		} else if (interval == AInterval.DAILY) {
			barSeries =  getHistoricalAboveDaily(symbol, interval, from, to);
	    } else {
	        assert false : "Yahoo fetcher support only one min and daily historical quotes!";
	    }
	    LOG.info("Historical data was retrieved successfully!");
		return barSeries;
	}
	
	public void setOneMinQuotesURLPattern(String pattern) {
	    ONE_MIN_QUOTES_URL_PATTERN = pattern;
	}
	
	private String[] convertYahoointraDayCsvToQuoteCsv(String[] csvLine, String symbol, AInterval interval) {
	    String[] quoteCsv = new String[8];
        quoteCsv[0] = symbol;
        quoteCsv[1] = csvLine[4];
        quoteCsv[2] = csvLine[2];
        quoteCsv[3] = csvLine[3];
        quoteCsv[4] = csvLine[1];
        quoteCsv[5] = csvLine[5];
        quoteCsv[6] = interval.toString();
        quoteCsv[7] = (new ATime(Integer.parseInt(csvLine[0]))).formattedString();
        return quoteCsv; 
	}
	
	public ABarSeries parseQuotesFromURL(String urlStr, String symbol, AInterval interval, ATime from, ATime to) throws Exception {
	    URL url;
	    if (urlStr.contains("http://")) {
	        url = new URL(urlStr);
	    } else {
	        url = new File(urlStr).toURI().toURL();
	    }
	    from = from.roundToXMin(interval);
	    if (to == null) {
	        to = ATime.now();
	    }
        to   = to.roundToXMin(interval);
	    LOG.info("Parsing quotes from url: " + urlStr + String.format(". symbol=%s. interval=%s. from=%s. to=%s.", 
	                                                                  symbol, interval.toString(), from.toString(), to.toString()));	  
	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = null;        
        ABarSeries barSeries = new ABarSeries(symbol, interval);
        LOG.debug(String.format("%d, %d", from.getDateTime().getMillis(), to.getDateTime().getMillis()));
        while (true) {
            line = in.readLine();
            if (line == null)
                break;
            if ((Character.isLetter(line.charAt(0))))
                continue;            
            String[] csvLine = line.split(",");
            String[] quoteCsv = convertYahoointraDayCsvToQuoteCsv(csvLine, symbol, interval);
            AQuote quote = (AQuote)((new AQuote()).initFromCSV(quoteCsv));
            quote.time(quote.time().roundToXMin(interval));
            if (quote.time().before(from))
                continue;
            if (to.before(quote.time()))
                break;
            LOG.debug(String.format("%d", quote.time().getDateTime().getMillis()));
            barSeries.addQuote(quote);
        }
        return barSeries;
    }
        
	private ABarSeries getHistoricalIntraDay(String symbol, AInterval interval, ATime from, ATime to) {
	    ABarSeries barSeries = null;
	    try {
	        long days = ATime.durationInDays(from, ATime.now()); 
	        String urlStr = getQuotesUrl(symbol, (int)days);
	        barSeries = parseQuotesFromURL(urlStr, symbol ,interval, from, to);
	    } catch (Exception e) {
	        e.printStackTrace();
	        assert false : "Yahoo couldnt get intraday quotes";	        
	    }	    
		return barSeries;
	}
	
	private String getQuotesUrl(String symbol, int days) {	    	    
	    String duration = String.valueOf(days) + "d";
	    return (ONE_MIN_QUOTES_URL_PATTERN.replaceAll("RANGE", duration)).replaceAll("TICKER", symbol);
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
