package org.gitprof.alcolil.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.gitprof.alcolil.common.ABarSeries;
import org.gitprof.alcolil.common.AInterval;
import org.gitprof.alcolil.common.ATime;


import org.gitprof.alcolil.unittests.NonMockedTest;
import org.gitprof.alcolil.unittests.SuperTestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.Stock;

/**
 * Unit test for simple App.
 */

@RunWith(PowerMockRunner.class)
public class YahooFetcherTest extends SuperTestCase {   
    
    public YahooFetcherTest() {
        super();
    }
    
    @Test
    public void testGetHistoricalIntraDay() throws Exception {
        YahooFetcher fetcher = new YahooFetcher();    
        fetcher.utils.QUOTES_URL_PATTERN = yahooQuotesTestFilePattern;
        /* should be 10 quotes between them (inclusive)*/
        ATime from = new ATime(1493993040L);
        ATime to = new ATime(1493995741L);         
        ABarSeries barSeries = fetcher.getHistoricalData("GOOG", AInterval.ONE_MIN, from, to);
        assertEquals("GOOG", barSeries.getSymbol());
        assertEquals(AInterval.ONE_MIN, barSeries.getInterval());
        assertEquals(10, barSeries.size());
        double eps = 0.0001;
        assertEquals(930.7500, barSeries.getQuote(1).high().doubleValue(), eps);
    }
    
    @SuppressWarnings("static-access")
	@Test
    public void testGetHistoricalAboveDaily() throws Exception {
        YahooFetcher fetcher = new YahooFetcher();
        
        // mock yahoo historical data
        // YahooFinance yahooAPI = Mockito.mock(YahooFinance.class);
        yahoofinance.Stock stock = PowerMockito.mock(yahoofinance.Stock.class);
        List<HistoricalQuote> histQuotes = Arrays.asList(
        	new HistoricalQuote("MSFT", null, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", null, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", null, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", null, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", null, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L)
        );
        PowerMockito.when(stock.getHistory()).thenReturn(histQuotes);
        PowerMockito.mockStatic(YahooFinance.class);
        BDDMockito.given(YahooFinance.get("MSFT", true)).willReturn(stock);
        
        ATime from = new ATime(1493993040L);
        ATime to = new ATime(1493995741L);   
        ABarSeries barSeries = fetcher.getHistoricalData("MSFT", AInterval.DAILY, from, to);
        assertEquals("MSFT", barSeries.getSymbol());
        assertEquals(AInterval.DAILY, barSeries.getInterval());
        assertEquals(5, barSeries.size());
        
        PowerMockito.verifyStatic(YahooFinance.class);
        Mockito.verify(stock).getHistory(yahoofinance.histquotes.Interval.DAILY);
    }
}