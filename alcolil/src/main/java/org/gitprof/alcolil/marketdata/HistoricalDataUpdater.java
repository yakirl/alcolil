package org.gitprof.alcolil.marketdata;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.scanner.BackTestPipe;

/*
 * this Class maintaining the database and keep it updated
 * once in a while the user should operate this module in order to retrieve info from marketdata sources
 * since most of the sources allow limited data fetching (only recent data)
 */
public class HistoricalDataUpdater {

	//private static String symbolFile;
	// we use pipe and not directly marketDataFetcher
	private AStockCollection stocks;
	private BackTestPipe quotePipe;
	
	public HistoricalDataUpdater(AStockCollection stocks) {
		this.stocks = stocks;
		//this.quotePipe  = new BackTestPipe();
	}
	
	// takes historicalData from remote - using QuotePipe
	public ATimeSeries getRemoteHistoricalData(AStock stock) {
		AStockCollection tmpStocks = new AStockCollection();
		tmpStocks.add(stock);
		ATimeSeries timeSeries = new ATimeSeries(stock.getSymbol());
		BackTestPipe quotePipe = new BackTestPipe(tmpStocks);
		Thread quotePipeThread = new Thread(quotePipe);
		quotePipeThread.start(); // return immediately
		AQuote quote;
		while(true) {
			quote = quotePipe.getNextQuote();
			if (quote.isDead()) {
				break;
			}
			timeSeries.add1minQuote(quote);
		}
		return timeSeries;
	}
	
	// takes historicalData from DB
	public ATimeSeries getLocalTimeSeries(String symbol) throws IOException {
		return DBManager.getInstance().readFromQuoteDB(symbol);
	}
	
	public Map<String, ATimeSeries> getLocalStockSeries(String[] symbols) throws IOException {
		Map<String, ATimeSeries> stockSeries = new HashMap<String, ATimeSeries>();
		for (String symbol : symbols) {
			stockSeries.put(symbol,getLocalTimeSeries(symbol));
		}
		return stockSeries;
	}
	
	
	public void updateQuoteDB() throws IOException {
		ATimeSeries remoteTimeSeries, localTimeSeries, mergedTimeSeries;
		for (AStock stock : stocks) {
			remoteTimeSeries = getRemoteHistoricalData(stock);
			localTimeSeries  = getLocalTimeSeries(stock.getSymbol());
			mergedTimeSeries = ATimeSeries.mergeTimeSeries(remoteTimeSeries, localTimeSeries);
			DBManager.getInstance().rewriteToQuoteDB(mergedTimeSeries);
		}
	}
}
