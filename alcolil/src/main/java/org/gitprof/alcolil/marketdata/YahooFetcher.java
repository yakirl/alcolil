package org.gitprof.alcolil.marketdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import org.gitprof.alcolil.common.ATimeSeries;

public class YahooFetcher extends BaseFetcher {

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void startRealTimeFetching() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StopRealTimeFetching() {
		// TODO Auto-generated method stub
		
	}
	
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
	
	@Override
	public ATimeSeries getHistory() {
		
		Stock stock;
		boolean toPrint = false;
		List<HistoricalQuote> histQuotes = null;
		try {
			stock = YahooFinance.get("TSLA", true);
			Interval interval = Interval.DAILY;
			histQuotes = stock.getHistory(interval);
			if (toPrint) {
				for (HistoricalQuote quote : histQuotes) {
					Integer year      = quote.getDate().get(Calendar.YEAR);
					Integer month      = quote.getDate().get(Calendar.MONTH) + 1;
					Integer dayOfMonth = quote.getDate().get(Calendar.DAY_OF_MONTH);
					//show(dayOfMonth.toString() + "." + month.toString() + "." + year.toString());
					//show(quote.getDate().getTime().toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ATimeSeries aTimeSeries = null; // convert
		return aTimeSeries;
	}

}
