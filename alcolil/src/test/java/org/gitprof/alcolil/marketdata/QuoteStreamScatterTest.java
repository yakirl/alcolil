package org.gitprof.alcolil.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.gitprof.alcolil.common.Interval;
import org.gitprof.alcolil.common.Quote;
import org.gitprof.alcolil.common.StockSeries;
import org.gitprof.alcolil.database.FileSystemDBManager;
import org.gitprof.alcolil.unittests.SuperTestCase;

import org.junit.Test;


/**
 * Unit test for QuoteStreamScatter
 */

public class QuoteStreamScatterTest extends SuperTestCase {

    public QuoteStreamScatterTest() {
        super();
    }
    
    @Test
    public void testJobExecutionLoopSync() throws Exception{
        List<String> symbols = new ArrayList<String>();
        symbols.add("GOOG_EXAMPLE");
        symbols.add("MSFT_EXAMPLE");
        // TODO: remove dbManager dependency
        StockSeries stockSeries = FileSystemDBManager.getInstance().readFromQuoteDB(symbols, Interval.ONE_MIN);        
        QuoteQueue quoteQueue = new QuoteQueue();
        QuoteStreamScatter scatter = new QuoteStreamScatter(quoteQueue, stockSeries);
        (new Thread() {
        	@Override
        	public void run() {
        		scatter.startStreaming();
        	}
        }).start();
        Quote quote;
        int googCount = 0, msftCount = 0;
        boolean foundSomeOpen = false;
        double someOpen = 2.02;
        while (true) {
            quote = quoteQueue.pop();
            if (quote == null)
                break;
            if (quote.symbol().equals("GOOG_EXAMPLE")) {
                googCount++;
                foundSomeOpen = (quote.open().doubleValue() == someOpen);
            }
            else if (quote.symbol().equals("MSFT_EXAMPLE")) {
                msftCount++;
            } else {
                assertTrue("found unrecognized quote symbol " + quote.symbol(), false);
            }
        }
        assertEquals(3, googCount);
        assertEquals(1, msftCount);       
        assertTrue(String.format("a quote with open price %f wasnt found!", someOpen), foundSomeOpen);
    }
}