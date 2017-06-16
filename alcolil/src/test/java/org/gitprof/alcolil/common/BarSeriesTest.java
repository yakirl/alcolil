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
        ABarSeries barSeries1 = new ABarSeries(symbol, AInterval.ONE_MIN);
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407360)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407420)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407480)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407540)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407600)));        
        ABarSeries barSeries2 = new ABarSeries(symbol, AInterval.ONE_MIN);
        barSeries2.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407660)));
        barSeries2.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407720)));
        barSeries2.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407780)));        
        ABarSeries mergedBarSeries = ABarSeries.mergeBarSeries(barSeries1, barSeries2);
        assertEquals(AInterval.ONE_MIN, mergedBarSeries.getInterval());
        assertEquals("EXAMPLE", mergedBarSeries.getSymbol());
        assertEquals(barSeries1.size() + barSeries2.size(), mergedBarSeries.size());
        assertEquals(barSeries1.getQuote(1).time(), mergedBarSeries.getQuote(1).time());
        assertEquals(barSeries2.getQuote(0).time(), mergedBarSeries.getQuote(5).time());
        mergedBarSeries = ABarSeries.mergeBarSeries(barSeries2, barSeries1);
        assertEquals(barSeries1.size() + barSeries2.size(), mergedBarSeries.size());
        assertEquals(barSeries1.getQuote(1).time(), mergedBarSeries.getQuote(1).time());
        assertEquals(barSeries2.getQuote(0).time(), mergedBarSeries.getQuote(5).time());             
    }
    
    @Test
    public void testBarSeriesIteration() {
        String symbol = "EXAMPLE";
        ABarSeries barSeries1 = new ABarSeries(symbol, AInterval.ONE_MIN);
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407360L)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407420L)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407480L)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407540L)));
        barSeries1.addQuote(new AQuote(symbol, 5.34, 7.7, 3.4, 4.0, 1200L, AInterval.ONE_MIN, new ATime(7407600L)));
        
        /* first iteration method: using list iterator - create in every iteration */
        long seconds = 7407360;        
        for (AQuote quote : barSeries1) {
            assertEquals(seconds, quote.time().getDateTime().getMillis() / 1000L);
            seconds += 60;
        }
        /* second iteration method: internal queue. can be used once */
        seconds = 7407360;
        AQuote quote = null;
        int i; for (i = 0; i < 5; i++) {
            quote = barSeries1.nextQuote();
            LOG.debug(String.format("%s", quote));
            assertEquals(seconds, quote.time().getSeconds());
            seconds += 60;
        }                
        assertEquals(null, barSeries1.nextQuote());
    }

}
