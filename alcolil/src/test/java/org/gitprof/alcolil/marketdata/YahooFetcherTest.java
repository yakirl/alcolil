package org.gitprof.alcolil.marketdata;

import static org.junit.Assert.assertEquals;

import org.gitprof.alcolil.common.ABarSeries;
import org.gitprof.alcolil.common.AInterval;
import org.gitprof.alcolil.common.ATime;


import org.gitprof.alcolil.unittests.NonMockedTest;
import org.gitprof.alcolil.unittests.SuperTestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.mock.*;

/**
 * Unit test for simple App.
 */

public class YahooFetcherTest extends SuperTestCase {   
    
    public YahooFetcherTest() {
        super();
    }
    
    @Category({NonMockedTest.class})
    @Test
    public void testGetHistoricalIntraDay() throws Exception {
    	
        YahooFetcher fetcher = new YahooFetcher();        
        /* should be 10 quotes between them (inclusive)*/
        ATime from = new ATime(1493993040L);
        ATime to = new ATime(1493995741L);         
        ABarSeries barSeries = fetcher.getHistoricalData(AInterval.ONE_MIN, yahooQuotesTestFile1, "GOOG", AInterval.FIVE_MIN, from, to);
        assertEquals("GOOG", barSeries.getSymbol());
        assertEquals(AInterval.FIVE_MIN, barSeries.getInterval());
        assertEquals(10, barSeries.size());
        double eps = 0.0001;
        assertEquals(930.7500, barSeries.getQuote(1).high().doubleValue(), eps);
    }
    
    @Test
    public void testGetHistoricalAboveDaily() {
        // YahooFetcher fetcher = new YahooFetcher();             
        // ABarSeries barSeries = fetcher.getHistoricalData("GOOG", AInterval.ONE_MIN, from, to);
    }
    
}