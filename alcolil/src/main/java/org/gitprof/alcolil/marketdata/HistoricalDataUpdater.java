package org.gitprof.alcolil.marketdata;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManagerAPI;
import org.gitprof.alcolil.scanner.BackTestPipe;


/**********************************************************
 * this Class maintaining the database and keep it updated
 * once in a while the user should operate this module in order to retrieve info from marketdata sources
 * since most of the sources allow limited data fetching (only recent data)
 *******************************************************/

public class HistoricalDataUpdater {

    protected static final Logger LOG = LogManager.getLogger(HistoricalDataUpdater.class);
	private DBManagerAPI dbManager;
	// we use pipe and not directly marketDataFetcher
	private BackTestPipe pipe;
	
	public HistoricalDataUpdater(DBManagerAPI dbManager, BackTestPipe pipe) {
		this.dbManager = dbManager;
		this.pipe = pipe;
	}
	
	public void updateQuoteDB() throws Exception {
		StockCollection stocks = dbManager.getStockCollection();
		updateQuoteDB(stocks.getSymbols(), Interval.ONE_MIN);
	}
	
	public void updateQuoteDB(List<String> symbols, Interval interval) throws Exception {		
		StockSeries stockSeries = pipe.getRemoteHistoricalData();
		dbManager.appendToQuoteDB(stockSeries);	
	}				
}
