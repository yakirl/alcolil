package org.gitprof.alcolil.scanner;

import java.io.IOException;
import java.util.Map;
import java.util.List;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;
import org.gitprof.alcolil.marketdata.QuoteStreamScatter;
import org.gitprof.alcolil.marketdata.BaseFetcher;
import org.gitprof.alcolil.marketdata.QuoteQueue;
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

	AStockSeries stocks;
	Map<String, ATimeSeries> quotesMapping;
	AInterval interval;
	ATime start = null;
	ATime stop = null;
	DBManager dbManager;
	PipeSource pipeSource;
	
	public enum PipeSource {
		LOCAL, REMOTE
	}
	
	public BackTestPipe(PipeSource pipeSource, AStockSeries stocks, AInterval interval, ATime from, ATime to) {
		this.stocks = stocks;
		this.interval = interval;
		start = from;
		stop = to;
		dbManager = DBManager.getInstance();
		this.pipeSource = pipeSource;
		//getHistoricalData();
	}

	public BackTestPipe(AStockSeries stocks) {
		this.stocks = stocks;
		dbManager = DBManager.getInstance();
	}
	
	// TODO
	private void getRemoteHistoricalData(BaseFetcher fetcher) {
		fetcher.connect();
		
		fetcher.disconnect();
		
		
		// fetcher.fetchHistoricalData(symbol, graphInterval, from, to);
		
	}
	
	// no use of HistUpdater!
	private void startStreamingFromLocalDB() {
		try {
			List<String> symbols = stocks.getSymbolList();
			AStockSeries stockSeries = dbManager.readFromQuoteDB(symbols, interval); 
			quoteQueue = new QuoteQueue();
			QuoteStreamScatter quoteStreamScatter = new QuoteStreamScatter(quoteQueue, stocks);
			// Thread handlerThread = new Thread(quoteStreamingHandler);
			// handlerThread.start();
			quoteStreamScatter.startStreaming();
		} catch (IOException e) {
			e.printStackTrace();
			closePipe();
		}
	}

	private void fetchHistoricalDataFromRemote() {
	 // TODO	
	}
	
	@Override
	public void run() {
		if (PipeSource.LOCAL == pipeSource) 
			startStreamingFromLocalDB();
		else // REMOTE
			fetchHistoricalDataFromRemote();
	}

	
}
