package org.gitprof.alcolil.common;


// import org.jfree.util.Log;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.gitprof.alcolil.unittests.SuperTestCase;

public class BarSeriesTest extends SuperTestCase {
    
    public BarSeriesTest() {
        super();
    }
    
    @Test
    public void testMergeBarSeries() {
        String symbol = "EXAMPLE";
        BarSeries barSeries1 = new BarSeries(symbol, Interval.ONE_MIN);
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407360)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407420)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407480)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407540)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407600)));        
        BarSeries barSeries2 = new BarSeries(symbol, Interval.ONE_MIN);
        barSeries2.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407660)));
        barSeries2.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407720)));
        barSeries2.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407780)));        
        BarSeries mergedBarSeries = BarSeries.mergeBarSeries(barSeries1, barSeries2);
        assertEquals(Interval.ONE_MIN, mergedBarSeries.getInterval());
        assertEquals("EXAMPLE", mergedBarSeries.getSymbol());
        assertEquals(barSeries1.size() + barSeries2.size(), mergedBarSeries.size());
        assertEquals(barSeries1.getQuote(1).time(), mergedBarSeries.getQuote(1).time());
        assertEquals(barSeries2.getQuote(0).time(), mergedBarSeries.getQuote(5).time());
        mergedBarSeries = BarSeries.mergeBarSeries(barSeries2, barSeries1);
        assertEquals(barSeries1.size() + barSeries2.size(), mergedBarSeries.size());
        assertEquals(barSeries1.getQuote(1).time(), mergedBarSeries.getQuote(1).time());
        assertEquals(barSeries2.getQuote(0).time(), mergedBarSeries.getQuote(5).time());             
    }
    
    @Test
    public void testBarSeriesIteration() {
        String symbol = "EXAMPLE";
        BarSeries barSeries1 = new BarSeries(symbol, Interval.ONE_MIN);
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407360L)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407420L)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407480L)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407540L)));
        barSeries1.addQuote(new Quote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, Interval.ONE_MIN, new Time(7407600L)));
        
        /* first iteration method: using list iterator - create in every iteration */
        long seconds = 7407360;        
        for (Quote quote : barSeries1) {
            assertEquals(seconds, quote.time().getDateTime().getMillis() / 1000L);
            seconds += 60;
        }
        /* second iteration method: internal queue. can be used once */
        seconds = 7407360;
        Quote quote = null;
        int i; for (i = 0; i < 5; i++) {
            quote = barSeries1.nextQuote();
            LOG.debug(String.format("%s", quote));
            assertEquals(seconds, quote.time().getSeconds());
            seconds += 60;
        }                
        assertEquals(null, barSeries1.nextQuote());
    }

}
