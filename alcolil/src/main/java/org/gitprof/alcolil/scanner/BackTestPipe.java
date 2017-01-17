package org.gitprof.alcolil.scanner;

import java.io.IOException;
import java.util.Map;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
//import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;

public class BackTestPipe extends BaseQuotePipe {

	AStockCollection stocks;
	Map<String, ATimeSeries> quotesMapping;
	int delayBetweenQuotes = 0;
	ATime start;
	ATime stop;
	DBManager dbManager;
	
	public BackTestPipe(AStockCollection stocks, ATime from, ATime to) {
		this.stocks = stocks;
		start = from;
		stop = to;
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
	
	private void getHistoricalData(String symbol) {
		// try retrieve data from DB
		try {
			dbManager.readFromQuoteDB(symbol);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closePipe();
		}
		// check missing
		
		// retrieve missing data from fetcher
		
		// update missing data in DB
	}

	
}
