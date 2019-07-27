package org.yakirl.alcolil.systemtests;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.yakirl.alcolil.common.Interval;
import org.yakirl.alcolil.database.DBManagerAPI;
import org.yakirl.alcolil.database.IntegratedDBManager;
import org.yakirl.alcolil.marketdata.FetcherAPI;
import org.yakirl.alcolil.marketdata.HistoricalDataUpdater;
import org.yakirl.alcolil.marketdata.YahooFetcher;
import org.yakirl.alcolil.scanner.BackTestPipe;
import org.yakirl.alcolil.scanner.BackTestPipe.PipeSource;


public class DBUpdateTest {

	@Test
	public void testUpdateDB() throws Exception {
		DBManagerAPI dbManager = new IntegratedDBManager();
		FetcherAPI fetcher = new YahooFetcher();
		List<String> symbols = new ArrayList<String>();
		symbols.add("GOOG");
		symbols.add("AAPL");
		symbols.add("INTC");
		symbols.add("AMD");
		BackTestPipe pipe = new BackTestPipe(dbManager, fetcher, PipeSource.REMOTE, symbols, Interval.ONE_MIN);
		HistoricalDataUpdater updater = new HistoricalDataUpdater(dbManager, pipe);
		updater.updateQuoteDB();
	}
}

