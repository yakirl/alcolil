package org.gitprof.alcolil.scanner;

import java.util.List;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.marketdata.FetcherAPI;
import org.gitprof.alcolil.marketdata.YahooFetcher;

public class RealTimePipe extends BaseQuotePipe {

	Time start;
	List<String> symbols;
	Interval interval;
	FetcherAPI fetcher;
	
	public RealTimePipe(FetcherAPI fetcher, List<String> symbols, Time from) {
		this.symbols = symbols;
		this.fetcher = fetcher;
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
	public void run() {
		startStreaming();
	}

}
