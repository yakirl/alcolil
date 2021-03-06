package org.yakirl.alcolil.marketdata;

import java.lang.Thread;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.*;

import org.json.JSONObject;
import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.utils.Parsing;
import org.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

// import org.gitprof.alcolil.core.Core;


/*****************
 * @author yakir
 * 
 *  Yahoo Fetcher - fetch market data from Yahoo servers
 *  
 *  we use here 2 different methods to fetch data:
 *  for daily quotes, we use Yahoo API.
 *  for intraday we read directly from URLs, since the API doesnt offer intraday quotes
 *
 */

public class YahooFetcher implements FetcherAPI {

    private int MAXIMUM_DAYS_FOR_HISTORICAL_QUTOES = 10; // this is totally arbitrary :-\
	protected static final Logger LOG = LogManager.getLogger(YahooFetcher.class);
	QuoteStreamGather quoteStreamGather;
	Thread quoteStreamGatherThread = null;
	YahooFetcherUtils utils;
	YahooFinance yahooAPI;
	
	public YahooFetcher() {
		this.utils = new YahooFetcherUtils();
		this.yahooAPI = new YahooFinance();
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
	public void postRealTimeJobLine(String symbol, Interval AInterval, Time from) {
		assert false : "Yahoo fetcher doesnt support real-time quotes!";
	}
	
	public void postHistoricalDataJobLine(String symbol, Interval AInterval, Time from, Time to) {
		assert false : "Yahoo fetcher doesnt support async fetching of historical data!";		
	}
	
	@Override
	public BarSeries getHistoricalData(String symbol, Interval interval, Time from, Time to) {
	    assert (interval != null) && (symbol != null) : "getHistoricalDate: got nulls parameters!";	    
	    if (to == null)
	        to = Time.now();
	    if (from == null)
            from = new Time(to.getDateTime().minusDays(MAXIMUM_DAYS_FOR_HISTORICAL_QUTOES));
	    LOG.info(String.format("get historical data: symbol=%s. interval=%s. from=%s. to=%s",
	             symbol, interval.toString(), from.toString(), to.toString()));
		BarSeries barSeries = null;
	    if (interval == Interval.ONE_MIN) {
			barSeries =  getHistoricalIntraDay(symbol, interval, from, to);
		} else if (interval == Interval.DAILY) {
			barSeries =  getHistoricalAboveDaily(symbol, interval, from, to);
	    } else {
	        assert false : "Yahoo fetcher support only one min and daily historical quotes!";
	    }
	    LOG.info("Historical data was retrieved successfully!");
		return barSeries;
	}
	
	private String[] convertYahoointraDayCsvToQuoteCsv(String[] csvLine, String symbol, Interval interval) {
	    String[] quoteCsv = new String[8];
        quoteCsv[0] = symbol;
        quoteCsv[1] = csvLine[4];
        quoteCsv[2] = csvLine[2];
        quoteCsv[3] = csvLine[3];
        quoteCsv[4] = csvLine[1];
        quoteCsv[5] = csvLine[5];
        quoteCsv[6] = interval.toString();
        quoteCsv[7] = (new Time(Integer.parseInt(csvLine[0]))).formattedString();
        return quoteCsv; 
	}
	
	private BarSeries parseQuotesFromURL(String urlStr, String symbol, Interval interval, Time from, Time to) throws Exception {
	    URL url;
	    if (urlStr.contains("https://")) {
	        url = new URL(urlStr);
	    } else {
	        url = new File(urlStr).toURI().toURL();
	    }
	    from = from.roundToXMin(interval);
	    if (to == null) {
	        to = Time.now();
	    }
        to   = to.roundToXMin(interval);
	    LOG.info("Parsing quotes from uri: " + urlStr + String.format(". symbol=%s. interval=%s. from=%s. to=%s.", 
	                                                                  symbol, interval.toString(), from.toString(), to.toString()));
	    return quotesFromJSON(url, symbol, interval, from, to);
	}
	
	private BarSeries quotesFromJSON(URL url, String symbol, Interval interval, Time from, Time to) throws IOException {
		JSONObject json = Parsing.readJsonFromUrl(url.toString());
		JSONObject data = (JSONObject) json.getJSONObject("chart").getJSONArray("result").get(0);
		JSONArray timestamps, opens, highs, lows, closes, volumes; 
		timestamps = data.getJSONArray("timestamp");
		opens = ((JSONObject) data.getJSONObject("indicators").getJSONArray("quote").get(0)).getJSONArray("open");
		highs = ((JSONObject) data.getJSONObject("indicators").getJSONArray("quote").get(0)).getJSONArray("high");
		lows = ((JSONObject) data.getJSONObject("indicators").getJSONArray("quote").get(0)).getJSONArray("low");
		closes = ((JSONObject) data.getJSONObject("indicators").getJSONArray("quote").get(0)).getJSONArray("close");
		volumes = ((JSONObject) data.getJSONObject("indicators").getJSONArray("quote").get(0)).getJSONArray("volume");
		BarSeries barSeries = new BarSeries(symbol, interval);
		int deadQuotes = 0;
		for (int i = 0; i < timestamps.length(); i++) {
			try {
				Quote quote = new Quote(symbol,
										opens.getDouble(i),
										highs.getDouble(i),
										lows.getDouble(i),
										closes.getDouble(i),
										volumes.getLong(i),
										interval,
										new Time(timestamps.getLong(i)));
				barSeries.addQuote(quote);
			} catch (Exception e) {
				deadQuotes+=1;			
			}
		}
		LOG.info("total dead quotes: " + deadQuotes);
		return barSeries;
	}
	
	private BarSeries quotesFromCSV(URL url, String symbol, Interval interval, Time from, Time to) throws IOException {
	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = null;        
        BarSeries barSeries = new BarSeries(symbol, interval);
        // LOG.debug(String.format("%d, %d", from.getDateTime().getMillis(), to.getDateTime().getMillis()));
        while (true) {
            line = in.readLine();
            if (line == null)
                break;
            if ((Character.isLetter(line.charAt(0))))
                continue;            
            String[] csvLine = line.split(",");
            String[] quoteCsv = convertYahoointraDayCsvToQuoteCsv(csvLine, symbol, interval);
            Quote quote = (Quote)((new Quote()).initFromCSV(quoteCsv));
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
        
	private BarSeries getHistoricalIntraDay(String symbol, Interval interval, Time from, Time to) {
	    BarSeries barSeries = null;
	    try {
	    	// yahoo always returns last quotes so we ask all of them and filter only needed
	        // long days = ATime.durationInDays(from, ATime.now()); 
	        String urlStr = utils.getQuotesUrl(symbol, interval);
	        barSeries = parseQuotesFromURL(urlStr, symbol ,interval, from, to);
	    } catch (Exception e) {
	        e.printStackTrace();
	        assert false : "Yahoo couldnt get intraday quotes";	        
	    }	    
		return barSeries;
	}
	
	//returns daily quotes
	// TODO: currently range is ignored. should be best effort 
	@SuppressWarnings("static-access")
	private BarSeries getHistoricalAboveDaily(String symbol, Interval interval, Time from, Time to) {
		yahoofinance.Stock yahooStock;
		BarSeries barSeries = new BarSeries(symbol, interval);
		try {
			yahooStock = yahooAPI.get(symbol, true);
			yahoofinance.histquotes.Interval yahooInterval = convertToYahooInterval(interval);
			List<HistoricalQuote> histQuotes = yahooStock.getHistory(yahooInterval);
			for (HistoricalQuote yahooQuote : histQuotes) {
				barSeries.addQuote(convertFromYahooQuote(yahooQuote, interval));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return barSeries;
	}
	
	private Quote convertFromYahooQuote(HistoricalQuote yahooQuote, Interval interval) {
		Quote quote = new Quote(yahooQuote.getSymbol(),
				yahooQuote.getOpen(),
				yahooQuote.getHigh(),
				yahooQuote.getLow(),
				yahooQuote.getClose(),
				yahooQuote.getVolume().intValue(),
				interval,
				convertFromYahooDate(yahooQuote.getDate()));
		return quote;								
	}
	
	private Time convertFromYahooDate(Calendar date) {
		return new Time(LocalDate.fromCalendarFields(date).toDateTimeAtCurrentTime());
	}
	
	private yahoofinance.histquotes.Interval convertToYahooInterval(Interval interval) {
		assert interval == Interval.DAILY : "conversion is not supported for non daily interval";
	    return yahoofinance.histquotes.Interval.DAILY;
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
