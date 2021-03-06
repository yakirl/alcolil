package org.yakirl.alcolil.marketdata;

import org.yakirl.alcolil.common.*;

/******************************************************************
 * 
 * Fetcher Interface:
 * 
 * a MarketDataFetcher has 2 primary functionalities:
 *  - get real time quotes - only 1min bars for now
 *  - get historical data - 1min and daily bars.
 *  
 *  historical data job line: (symbol, interval, time_range)
 *  real time data job line: (symbol, interval, start (None?))
 *  
 *  Operation
 *  --------------
 *  
 *  General interface (the same interface is offered for both real time and historical fetching):
 *   1. connect() - Open a connection to a remote data server, and perform some initialization operations
 *   2. activateStreaming() - gets QuoteQueue. this object has a push() method for inserting new quote.
 *   		this will start new streaming - a DataHandler thread that will busy waiting on the data source (remote server) 
 *          and every new candle/tick it gets - push it	to the QuoteQueue that was initialized. thus QuoteQueue must synchronized 
 *          the access to its queue. every job the user will post - its quotes will be pushed into this queue.
 *   		currently we support 1 streaming (1 queue)
 *   3. start posting jobs: with postRealTimeJobLine() or postHistoricalDataJobLine().
 *          after receiving a new job, the fetcher starts to retrieve quotes from remote, and pushing to the queue.
 *   		the Fetcher make the request, execute a handler in a separate thread to listen to the 
 *  		data that streaming back, tick by tick, or candle by candle, and return immediately.
 *   4. start polling on the QuoteQueue, popping and processing quotes.
 *   5. when done, call deactivateStreaming() to stop the streaming.
 *   6. disconnect()
 *   
 *  another interface can be implemented for Historical Data Fetching:
 *   1. connect()
 *   2. fetchHistoricalData() - return the whole data at once
 *   3. disconnect() - Close remote server connection and perform tear down operations.
 *  
 ********************************************************************************************/


public interface FetcherAPI {
		
	public abstract void connect();
	
	public abstract void disconnect();
	
	// start the streaming thread
	public abstract void activateStreaming(QuoteQueue quoteQueue);
	
	// stop the streaming thread
	public abstract void deactivateStreaming();
	
	public abstract void postHistoricalDataJobLine(String symbol, Interval interval, Time from, Time to);
	
	// send to the streaming thread  a realtimeData request / job
	public abstract void postRealTimeJobLine(String symbol, Interval interval, Time from);	
	
	// get historical data
	public abstract BarSeries getHistoricalData(String symbol, Interval interval, Time from, Time to);
}
