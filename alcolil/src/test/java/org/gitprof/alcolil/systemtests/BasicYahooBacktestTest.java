package org.gitprof.alcolil.systemtests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.systemtests.BaseTestScenario;
import org.gitprof.alcolil.database.IntegratedDBManager;
import org.gitprof.alcolil.database.DBManagerAPI;
import org.gitprof.alcolil.marketdata.FetcherAPI;
import org.gitprof.alcolil.marketdata.YahooFetcher;
import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;
import org.gitprof.alcolil.scanner.BackTestPipe;

import org.junit.Test;


public class BasicYahooBacktestTest extends BaseTestScenario {

	@Test
	public void testBasic() throws Exception {
		DBManagerAPI dbManager = new IntegratedDBManager();
		FetcherAPI fetcher = new YahooFetcher();
		List<String> symbols = new ArrayList<String>(Arrays.asList("GOOG", "MSFT", "INTC"));
		BackTestPipe pipe = new BackTestPipe(dbManager, fetcher, BackTestPipe.PipeSource.REMOTE, symbols, Interval.ONE_MIN);
		HistoricalDataUpdater updater = new HistoricalDataUpdater(dbManager, pipe);
		updater.updateQuoteDB();
	}
}
