package org.gitprof.alcolil.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.gitprof.alcolil.common.AInterval;
import org.gitprof.alcolil.common.AQuote;
import org.gitprof.alcolil.common.AStockSeries;
import org.gitprof.alcolil.database.FileSystemDBManager;
import org.gitprof.alcolil.unittests.SuperTestCase;

/**
 * Unit test for simple App.
 */

public class QuoteStreamScatterTest extends SuperTestCase {

    public QuoteStreamScatterTest() {
        super();
    }
    
    public void testJobExecutionLoopSync() throws Exception{
        List<String> symbols = new ArrayList<String>();
        symbols.add("GOOG_EXAMPLE");
        symbols.add("MSFT_EXAMPLE");        
        AStockSeries stockSeries = FileSystemDBManager.getInstance().readFromQuoteDB(symbols, AInterval.ONE_MIN);        
        QuoteQueue quoteQueue = new QuoteQueue();
        QuoteStreamScatter scatter = new QuoteStreamScatter(quoteQueue, stockSeries);
        scatter.stop(); // we want that scatter will finish as soon as it is done all quotes
        scatter.run();
        AQuote quote;
        int googCount = 0, msftCount = 0;
        boolean foundSomeOpen = false;
        double someOpen = 2.02;
        while (true) {
            quote = quoteQueue.pop();
            if (quote == null)
                break;
            if (quote.symbol() == "GOOG_EXAMPLE") {
                googCount++;
                foundSomeOpen = (quote.open().doubleValue() == someOpen);
            }
            else if (quote.symbol() == "MSFT_EXAMPLE") {
                msftCount++;
            } else {
                assertTrue("found unrecognized quote symbol!", false);
            }
        }
        assertEquals(3, googCount);
        assertEquals(1, msftCount);       
        assertTrue(String.format("a quote with open price %d wasnt found!", someOpen), foundSomeOpen);
    }
}
