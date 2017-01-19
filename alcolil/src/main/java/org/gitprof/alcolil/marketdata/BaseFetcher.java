package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;

/*
 * Fetcher Interface:
 * 
 * a MarketDataFetcher has 2 primary functionalities:
 *  - get real time quotes - only 1min bars for now
 *  - get historical data - 1min and daily bars.
 *  
 *  each of them take only one symbol and one bar_size as parameters. so for multiple stock or multiple bar_size fetching
 *  the user needs to call it multiple times.
 *  
 *  the Operation is as follows:
 *  in construction, a Fetcher gets QuoteQueue. this object has a push() method for inserting new quote
 *  after calling to getHistoricalData() or getRealTimeData(), the Fetcher make the request, execute a handler in a seperate thread to listen to the 
 *  data that streaming back, tick by tick, or candle by candle, and return immediately.
 *  
 *  the DataHandler thread busy waiting on the data source (some remote server) and every new candle/tick it gets - push it 
 *  to the QuoteQueue that was initialized on construction.
 *  thus QuoteQueue must synchronized the access to its queue.
 *  
 *  must use connect() before any operation, and disconnect() after we are done.
 */
public abstract class BaseFetcher {

	QuoteQueue quoteQueue;
	
	public BaseFetcher() {
		//this.quoteQueue = quoteQueue;
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
	
	// start the streaming thread
	public abstract void activateStreaming(QuoteQueue quoteQueue);
	
	// stop the streaming thread
	public abstract void deactivateStreaming();
	
	// send to the streaming job a historicalData request / job
	public abstract void fetchHistoricalData(String symbol, Enums.GraphInterval graphInterval, ATime from, ATime to);
	
	// send to the streaming thread  a realtimeData request / job
	public abstract void fetchRealTimeData(String symbol, Enums.GraphInterval graphInterval, ATime from);	
	
	// this is optional - may be not implemented (in IB for example...)
	public abstract ABarSeries getHistoricalData(String symbol, Enums.GraphInterval graphInterval, ATime from, ATime to);
	
}
