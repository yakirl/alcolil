package org.yakirl.alcolil.systemtests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.database.DBManagerAPI;
import org.yakirl.alcolil.database.IntegratedDBManager;
import org.yakirl.alcolil.marketdata.FetcherAPI;
import org.yakirl.alcolil.marketdata.HistoricalDataUpdater;
import org.yakirl.alcolil.marketdata.YahooFetcher;
import org.yakirl.alcolil.scanner.BackTestPipe;
import org.yakirl.alcolil.systemtests.BaseTestScenario;


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
