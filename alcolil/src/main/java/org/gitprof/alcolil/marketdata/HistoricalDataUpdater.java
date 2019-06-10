package org.gitprof.alcolil.marketdata;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManagerAPI;
import org.gitprof.alcolil.scanner.BackTestPipe;
import org.gitprof.alcolil.marketdata.FetcherAPI;

/**********************************************************
 * this Class maintaining the database and keep it updated
 * once in a while the user should operate this module in order to retrieve info from marketdata sources
 * since most of the sources allow limited data fetching (only recent data)
 *******************************************************/

public class HistoricalDataUpdater {

	// we use pipe and not directly marketDataFetcher
    protected static final Logger LOG = LogManager.getLogger(HistoricalDataUpdater.class);
	private StockCollection stocks;
	private DBManagerAPI dbManager;
	private FetcherAPI fetcher;
	private BackTestPipe pipe;
	
	public HistoricalDataUpdater(DBManagerAPI dbManager, FetcherAPI fetcher, BackTestPipe pipe) {
		this.dbManager = dbManager;
		this.fetcher = fetcher;
		this.pipe = pipe;
	}
	
	public void updateQuoteDB() throws IOException {
		StockCollection stocks = dbManager.getStockCollection();
		updateQuoteDB(stocks.getSymbols(), Interval.ONE_MIN);
	}
	
	public void updateQuoteDB(List<String> symbols, Interval interval) throws IOException {
		BarSeries remoteBarSeries, localBarSeries, mergedBarSeries;
		StockSeries remoteStockSeries = getRemoteHistoricalData(symbols, interval);
		StockSeries localStockSeries  = getLocalStockSeries(symbols, interval);
		StockSeries mergedStockSeries = new StockSeries(interval);
		for (String symbol : symbols) {
			LOG.info(String.format("updating Quote DB for symbol: %s interval: %s", symbol, interval));
			remoteBarSeries = remoteStockSeries.getBarSeries(symbol);
			localBarSeries = localStockSeries.getBarSeries(symbol);
			mergedBarSeries = BarSeries.mergeBarSeries(remoteBarSeries, localBarSeries);
			mergedStockSeries.addBarSeries(symbol, mergedBarSeries);
		}
		dbManager.rewriteToQuoteDB(mergedStockSeries);
	}
	
	private StockSeries getRemoteHistoricalData(List<String> symbols, Interval interval) {
		StockSeries stockSeries = pipe.getRemoteHistoricalData();
		return stockSeries;
	}
			
	private StockSeries getLocalStockSeries(List<String> symbols, Interval interval) throws IOException {
		return dbManager.readFromQuoteDB(symbols, interval);
	}	
}
