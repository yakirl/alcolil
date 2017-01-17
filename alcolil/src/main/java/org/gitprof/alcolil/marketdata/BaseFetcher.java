package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;


public abstract class BaseFetcher {

	
	
	public BaseFetcher() {
		//quoteQueue = new QuoteQueue();
	}
	
	public static BaseFetcher getDefaultFetcher() {
		BaseFetcher fetcher;
		String fetcherName = "TODO";
		if("YahooFetcher" == fetcherName) {
			fetcher = new IBFetcher();
		} else {
			fetcher = new YahooFetcher();
		}
		return fetcher;
	}
	
	public abstract void connect();
	
	public abstract void disconnect();
	
	public abstract ATimeSeries getHistory(String symbol, Enums.GraphInterval graphInterval, ATime from, ATime to);
	
	public abstract void startRealTimeFetching();
		
	public abstract void StopRealTimeFetching();
	
	
}
