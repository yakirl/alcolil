package org.gitprof.alcolil.scanner;

import java.io.IOException;

import java.util.Map;
import java.util.List;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManagerAPI;
import org.gitprof.alcolil.database.FileSystemDBManager;
import org.gitprof.alcolil.marketdata.QuoteStreamScatter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.gitprof.alcolil.marketdata.QuoteQueue;


/**********************************************************
 * Supported interval fetching: ONE_MIN, FIVE_MIN, DAILY
 * 
 * 2 basic operations mode:
 *  - get data from local DB and stream it to the requesting module (usually scanner). in this case the Pipe will get the data chunk
 *  	at once from the DB, and will push quote by quote to the QuotePipe, by itself - no another thread is invoked.
 *  - get data from remote source and stream it to the requesting module (usually historicalDataUpdater)
 ***********************************************************/

public class BackTestPipe extends BaseQuotePipe {

    private static final Logger LOG = LogManager.getLogger(BackTestPipe.class);
	List<String> symbols;
	Map<String, TimeSeries> quotesMapping;
	Interval interval;
	Time start = null;
	Time stop = null;
	DBManagerAPI dbManager;
	PipeSource pipeSource;
	
	public enum PipeSource {
		LOCAL, REMOTE
	}
	
	public BackTestPipe(DBManagerAPI dbManager, PipeSource pipeSource, List<String> symbols, Interval interval, Time from, Time to) {
		this.symbols = symbols;
		this.interval = interval;
		start = from;
		stop = to;
		this.dbManager = dbManager;
		this.pipeSource = pipeSource;
	}

	public BackTestPipe(DBManagerAPI dbManager, PipeSource pipeSource, List<String> symbols, Interval interval) {
		this.symbols = symbols;
		this.interval = interval;
		this.dbManager = dbManager;
		this.pipeSource = pipeSource;
	}
	
	public StockSeries getRemoteHistoricalData() {
	    LOG.info("retrieve historical data from remote");
		fetcher.connect();
		StockSeries stockSeries = new StockSeries(interval);
		BarSeries barSeries;
		for (String symbol : symbols) {
			barSeries = fetcher.getHistoricalData(symbol, interval, start, stop);
			stockSeries.addBarSeries(symbol, barSeries);
		}
		fetcher.disconnect();
		LOG.info("data was retrieved successfully!");
		return stockSeries;
	}
	
	private void startStreamingFromLocalDB() {
		try {
			StockSeries stockSeries = dbManager.readFromQuoteDB(symbols, interval); 
			quoteQueue = new QuoteQueue();
			QuoteStreamScatter quoteStreamScatter = new QuoteStreamScatter(quoteQueue, stockSeries);
			quoteStreamScatter.startStreamingAsync();
		} catch (IOException e) {
			e.printStackTrace();
			closePipe();
		}
	}
	
	@Override
	public void run() {
		if (PipeSource.LOCAL == pipeSource) 
			startStreamingFromLocalDB();
		else // REMOTE
			assert false : "Backtest pipe doesnt support getting historical data async!";
	}	
}