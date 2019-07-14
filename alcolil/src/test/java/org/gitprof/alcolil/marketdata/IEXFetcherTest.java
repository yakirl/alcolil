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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Unit test for simple App.
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class IEXFetcherTest extends SuperTestCase {   
    
    public IEXFetcherTest() {
        super();
    }
    
    @Test
    public void test() throws Exception {
        IEXFetcher fetcher = new IEXFetcher();
        fetcher.printQuote();
        /*
        Time from = new Time(1493993040L);
        Time to = new Time(1493995741L);         
        BarSeries barSeries = fetcher.getHistoricalData("GOOG", Interval.ONE_MIN, from, to);
        assertEquals("GOOG", barSeries.getSymbol());
        assertEquals(Interval.ONE_MIN, barSeries.getInterval());
        assertEquals(10, barSeries.size());
        double eps = 0.0001;
        assertEquals(930.7500, barSeries.getQuote(1).high().doubleValue(), eps);
        */
    }
}