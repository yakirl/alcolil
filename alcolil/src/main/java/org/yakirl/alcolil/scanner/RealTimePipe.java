package org.yakirl.alcolil.scanner;

import java.util.List;

import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.marketdata.FetcherAPI;
import org.yakirl.alcolil.marketdata.YahooFetcher;

public class RealTimePipe extends BaseQuotePipe {

	Time start;
	List<String> symbols;
	Interval interval;
	FetcherAPI fetcher;
	
	public RealTimePipe(FetcherAPI fetcher, List<String> symbols, Time from) {
		super(fetcher);
		this.symbols = symbols;
		start = from;
		interval = Interval.ONE_MIN;
	}
	
	private void startStreaming() {
		fetcher.connect();
		fetcher.activateStreaming(quoteQueue);
		for (String symbol : symbols) {
			fetcher.postRealTimeJobLine(symbol, interval, start);
		}
		fetcher.deactivateStreaming();
		fetcher.disconnect();
	}

	@Override
	public void closePipe() {
		
	}
	
	@Override
	public void run() {
		startStreaming();
	}

}
