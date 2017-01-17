package org.gitprof.alcolil.scanner;

import java.util.Map;

import org.gitprof.alcolil.common.*;
//import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;

public class BackTestPipe extends BaseQuotePipe {

	AStockCollection stocks;
	Map<String, ATimeSeries> quotesMapping;
	int delayBetweenQuotes = 0;
	ATime start;
	ATime stop;
	
	public BackTestPipe(AStockCollection stocks, ATime from, ATime to) {
		this.stocks = stocks;
		start = from;
		stop = to;
		getHistoricalData();
	}
	
	@Override
	public AQuote getNextQuote() {
		AQuote nextQuote = quoteQueuePoll(3);
		return nextQuote;
	}
	
	private void getHistoricalData() {
		// try retrieve data from DB
		
		// check missing
		
		// retrieve missing data from fetcher
		
		// update missing data in DB
	}

	
}
