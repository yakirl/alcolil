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
	//private BackTestPipe quotePipe;
	
	public HistoricalDataUpdater(AStockCollection stocks) {
		this.stocks = stocks;
		//this.quotePipe  = new BackTestPipe();
	}
	
	public HistoricalDataUpdater(String stocksFilename) throws IOException {
		this.stocks = DBManager.getInstance().getStockCollection();
		//this.quotePipe  = new BackTestPipe();
	}
	
	// takes historicalData from remote - using QuotePipe
	private ATimeSeries getRemoteHistoricalData(AStock stock) {
		AStockCollection tmpStocks = new AStockCollection();
		tmpStocks.add(stock);
		ATimeSeries timeSeries = new ATimeSeries(stock.getSymbol());
		BackTestPipe quotePipe = new BackTestPipe(tmpStocks);
		Thread quotePipeThread = new Thread(quotePipe);
		quotePipeThread.start();
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
	private ATimeSeries getLocalTimeSeries(String symbol) throws IOException {
		return DBManager.getInstance().readFromQuoteDB(symbol);
	}
	
	private AStockSeries getLocalStockSeries(String[] symbols) throws IOException {
		AStockSeries stockSeries = new AStockSeries();
		for (String symbol : symbols) {
			stockSeries.addTimeSeries(symbol, getLocalTimeSeries(symbol));
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
