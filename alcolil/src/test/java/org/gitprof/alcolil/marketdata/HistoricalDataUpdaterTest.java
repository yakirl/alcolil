package org.gitprof.alcolil.marketdata;

import java.io.IOException;
import java.util.ArrayList;

import org.gitprof.alcolil.common.AInterval;
import org.gitprof.alcolil.common.AStockSeries;
import org.gitprof.alcolil.scanner.BackTestPipe;
import org.gitprof.alcolil.unittests.SuperTestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;;

public class HistoricalDataUpdaterTest extends SuperTestCase {

    public HistoricalDataUpdaterTest() {
        super();
    }
    
    @Test
    public void testUpdateQuoteDB() throws IOException {
        YahooFetcher fetcher = new YahooFetcher();
        ArrayList<String> symbols = new ArrayList<String>();
        symbols.add("GOOG_EXAMPLE");
                
        fetcher.setOneMinQuotesURLPattern(yahooQuotesTestFile1);
        HistoricalDataUpdater updater = new HistoricalDataUpdater(fetcher);       
        updater.updateQuoteDB(symbols, AInterval.ONE_MIN);
        // read from quote DB and verify writing
        fetcher.setOneMinQuotesURLPattern(yahooQuotesTestFile2);
        updater = new HistoricalDataUpdater(fetcher);
        updater.updateQuoteDB(symbols, AInterval.ONE_MIN);
        // read from quote DB and verify merging
    }
    
    @Test
    public void testWithMocks() {
        Mockery context = new Mockery();
        BackTestPipe mockedPipe = context.mock(org.gitprof.alcolil.scanner.BackTestPipe.class);
        context.checking(new Expectations() {{
            atLeast(1).of(mockedPipe).getRemoteHistoricalData();
            will(returnValue(AStockSeries.class));
        }});
        context.assertIsSatisfied();
        
    }
}
