package org.gitprof.alcolil.scanner;

import java.io.IOException;
import java.util.Map;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;
//import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;

/*
 * Supported interval fetching: ONE_MIN, FIVE_MIN, DAILY
 * 
 * 2 basic operations mode:
 *  - get data from local DB and stream it to the requesting module (usually scanner). in this case the Pipe will get the data chunk
 *  	at once from the DB, and will push quote by quote to the QuotePipe, by itself - no another thread is invoked.
 *  - get data from remote source and stream it to the requesting module (usually historicalDataUpdater)
 */
public class BackTestPipe extends BaseQuotePipe {

	AStockCollection stocks;
	Map<String, ATimeSeries> quotesMapping;
	int delayBetweenQuotes = 0;
	ATime start = null;
	ATime stop = null;
	DBManager dbManager;
	
	public BackTestPipe(AStockCollection stocks, ATime from, ATime to) {
		this.stocks = stocks;
		start = from;
		stop = to;
		dbManager = DBManager.getInstance();
		//getHistoricalData();
	}

	public BackTestPipe(AStockCollection stocks) {
		this.stocks = stocks;
		dbManager = DBManager.getInstance();
		//getHistoricalData();
	}
	
	@Override
	public AQuote getNextQuote() {
		AQuote nextQuote = quoteQueuePoll(3);
		return nextQuote;
	}
	
	private void closePipe() {
		
	}
	
	private void pushQuotesLoop() {
		while(true) {
			try {
				Thread.sleep(delayBetweenQuotes);  // should do something nicer than that...
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// if we are done pushing than push dead Quote
			
		}
	}
	
	// use marketDataFetcher
	private void getRemoteHistoricalData() {
		
	}
	
	// use HistoricalDataUpdater
	private void getLocalHistoricalData(AStockCollection stocks) {
		try {
			HistoricalDataUpdater updater = new HistoricalDataUpdater(stocks);
			updater.getLocalStockSeries(stocks.getSymbols());
		} catch (IOException e) {
			e.printStackTrace();
			closePipe();
		}
		pushQuotesLoop();
		
	}

	@Override
	public void run() {
		getLocalHistoricalData();
		
	}

	
}
