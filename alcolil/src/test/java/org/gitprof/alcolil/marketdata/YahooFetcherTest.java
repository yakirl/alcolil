package org.gitprof.alcolil.marketdata;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.gitprof.alcolil.common.BarSeries;
import org.gitprof.alcolil.common.Interval;
import org.gitprof.alcolil.common.Time;

import org.gitprof.alcolil.unittests.SuperTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

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
        Time from = new Time(1493993040L);
        Time to = new Time(1493995741L);         
        BarSeries barSeries = fetcher.getHistoricalData("GOOG", Interval.ONE_MIN, from, to);
        assertEquals("GOOG", barSeries.getSymbol());
        assertEquals(Interval.ONE_MIN, barSeries.getInterval());
        assertEquals(10, barSeries.size());
        double eps = 0.0001;
        assertEquals(930.7500, barSeries.getQuote(1).high().doubleValue(), eps);
    }
    
    @SuppressWarnings("static-access")
    @PrepareForTest({YahooFinance.class})
	@Test
    public void testGetHistoricalAboveDaily() throws Exception {
        YahooFetcher fetcher = new YahooFetcher();
        
        // mock yahoo historical data
        // YahooFinance yahooAPI = Mockito.mock(YahooFinance.class);
       yahoofinance.Stock stock = Mockito.mock(yahoofinance.Stock.class);
       Calendar cal = Calendar.getInstance();
       cal.setTimeInMillis(102320L);
       List<HistoricalQuote> histQuotes = Arrays.asList(
        	new HistoricalQuote("MSFT", cal, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", cal, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", cal, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", cal, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L),
        	new HistoricalQuote("MSFT", cal, new BigDecimal(1.1), new BigDecimal(0.4), new BigDecimal(2.3), new BigDecimal(1.02), new BigDecimal(2.1), 1023L)
        );
        Mockito.when(stock.getHistory(yahoofinance.histquotes.Interval.DAILY)).thenReturn(histQuotes);
        PowerMockito.mockStatic(YahooFinance.class);
        BDDMockito.given(YahooFinance.get("MSFT", true)).willReturn(stock);
        
        Time from = new Time(1493993040L);
        Time to = new Time(1493995741L);   
        BarSeries barSeries = fetcher.getHistoricalData("MSFT", Interval.DAILY, from, to);
        assertEquals("MSFT", barSeries.getSymbol());
        assertEquals(Interval.DAILY, barSeries.getInterval());
        assertEquals(5, barSeries.size());
        
        PowerMockito.verifyStatic(YahooFinance.class);
        YahooFinance.get("MSFT", true);
        
        Mockito.verify(stock).getHistory(yahoofinance.histquotes.Interval.DAILY);
    }
}