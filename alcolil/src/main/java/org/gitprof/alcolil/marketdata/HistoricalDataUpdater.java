package org.gitprof.alcolil.marketdata;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.scanner.BackTestPipe;
import org.gitprof.alcolil.marketdata.FetcherAPI;

/*
 * this Class maintaining the database and keep it updated
 * once in a while the user should operate this module in order to retrieve info from marketdata sources
 * since most of the sources allow limited data fetching (only recent data)
 */
public class HistoricalDataUpdater {

	// we use pipe and not directly marketDataFetcher
    protected static final Logger LOG = LogManager.getLogger(HistoricalDataUpdater.class);
	private AStockCollection stocks;
	private DBManager dbManager;
	private FetcherAPI fetcher;
	//private BackTestPipe quotePipe;
	
	public HistoricalDataUpdater() {
		this.dbManager = DBManager.getInstance();
		this.fetcher = new YahooFetcher();
	}

	public HistoricalDataUpdater(FetcherAPI fetcher) {
		this.dbManager = DBManager.getInstance();
		this.fetcher = fetcher;
	}
	
	private AStockSeries getRemoteHistoricalData(List<String> symbols, AInterval interval) {
		BackTestPipe quotePipe = new BackTestPipe(symbols, interval);
		AStockSeries stockSeries = quotePipe.getRemoteHistoricalData();
		return stockSeries;
	}
			
	private AStockSeries getLocalStockSeries(List<String> symbols, AInterval interval) throws IOException {
		return dbManager.readFromQuoteDB(symbols, interval);
	}
	
	public void updateQuoteDB() throws IOException {
		AStockCollection stocks = dbManager.getStockCollection();
		updateQuoteDB(stocks.getSymbols(), AInterval.ONE_MIN);
	}
	
	public void updateQuoteDB(List<String> symbols, AInterval interval) throws IOException {
	    LOG.info("updating Quote DB");
		ABarSeries remoteBarSeries, localBarSeries, mergedBarSeries;
		AStockSeries remoteStockSeries = getRemoteHistoricalData(symbols, interval);
		AStockSeries localStockSeries  = getLocalStockSeries(symbols, interval);
		AStockSeries mergedStockSeries = new AStockSeries(interval);
		for (String symbol : symbols) {
			remoteBarSeries = remoteStockSeries.getBarSeries(symbol);
			localBarSeries = localStockSeries.getBarSeries(symbol);
			mergedBarSeries = ABarSeries.mergeBarSeries(remoteBarSeries, localBarSeries);
			mergedStockSeries.addBarSeries(symbol, mergedBarSeries);
		}
		dbManager.rewriteToQuoteDB(mergedStockSeries);
	}
}
