package org.yakirl.alcolil.database;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;

import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.database.Conf;
import org.yakirl.alcolil.database.FileSystemDBManager;
import org.yakirl.alcolil.database.MarketStoreDBManager;
import org.yakirl.alcolil.unittests.SuperTestCase;



/**
 * Unit test for DBManager.
 * 
 * A real marketstore instance is used for the UT
 */

public class MarketStoreDBManagerTest extends SuperTestCase {

	protected static final Logger LOG = LogManager.getLogger(FileSystemDBManagerTest.class);
	private MarketStoreDBManager dbManager;
	private Conf conf;
	
	public MarketStoreDBManagerTest() throws Exception {
        super();
        dbManager = (MarketStoreDBManager) MarketStoreDBManager.getInstance();
        conf = new Conf();
    }
    
	@Before
    public void setUp() throws Exception {
    	LOG.info("setUp test");
    	// dbManager.validateDBStructure();
    }
    
	@After
    public void tearDown() throws Exception {
    	LOG.info("tearDown test");
    	// dbManager.close();
    }   	
    
    @Test
    public void testReadWriteSimple() throws Exception {
    	//TimeSeries intcTimeSeries = new TimeSeries("INTC");
    	Quote quote;
    	String symbol = RandomStringUtils.random(4, true, false);
    	quote = new Quote(symbol, 1.02, 3.60, 0.60, 1.43, 20500, Interval.ONE_MIN, new Time(646721399L));
    	BarSeries barSeries = new BarSeries(symbol, Interval.ONE_MIN);
    	barSeries.addQuote(quote);
    	dbManager.appendToQuoteDB(barSeries);
    	
    	BarSeries remoteBarSeries = dbManager.readFromQuoteDB(symbol, Interval.ONE_MIN);
        // BarSeries remoteBarSeries = intcTimeSeriesInput.getBarSeries(Interval.ONE_MIN);
        assertEqualsDbl(1.02, remoteBarSeries.getQuote(0).open().doubleValue());
        assertEqualsDbl(3.60, remoteBarSeries.getQuote(0).high().doubleValue());
        assertEqualsDbl(0.60, remoteBarSeries.getQuote(0).low().doubleValue());
        assertEqualsDbl(1.43, remoteBarSeries.getQuote(0).close().doubleValue());
        assertEquals(20500, remoteBarSeries.getQuote(0).volume());
        assertEquals(Interval.ONE_MIN, remoteBarSeries.getQuote(0).interval());
    }
    
    
    private Quote genQuote(String symbol, int timeOffset) {
		double upLimit = 50.0, bottomLimit = 0.0, candleRange=8.0;
		double low = bottomLimit + Math.random() * (upLimit - bottomLimit);
		double high = low + Math.random() * candleRange;
		double close = low + Math.random() * (high - low);
		double open = low + Math.random() * (high - low);
		long volume = (long)(Math.random() * 1000.0);
		long epoch = 1564040054L + ((long)timeOffset * 60L);
		return new Quote(symbol, open, high, low, close, volume, Interval.ONE_MIN, new Time(epoch));
	}
    
    @Test
    public void testReadWriteBigData() throws Exception {
    	String symbol = RandomStringUtils.random(4, true, false);
    	BarSeries barSeries = new BarSeries(symbol, Interval.ONE_MIN);
    	int range = 50;
    	    	    	
    	int i; for(i = 0; i < range; i++) {
    		barSeries.addQuote(genQuote(symbol, i));
    	}

    	dbManager.appendToQuoteDB(barSeries);
    	
    	BarSeries remoteBarSeries = dbManager.readFromQuoteDB(symbol, Interval.ONE_MIN);
    	
    	for(i = 0; i < range; i++) {
    		assertEqualsDbl(barSeries.getQuote(i).open().doubleValue(), remoteBarSeries.getQuote(i).open().doubleValue());
    		assertEqualsDbl(barSeries.getQuote(i).high().doubleValue(), remoteBarSeries.getQuote(i).high().doubleValue());
    		assertEqualsDbl(barSeries.getQuote(i).low().doubleValue(), remoteBarSeries.getQuote(i).low().doubleValue());
    		assertEqualsDbl(barSeries.getQuote(i).close().doubleValue(), remoteBarSeries.getQuote(i).close().doubleValue());    		
    		assertEquals(barSeries.getQuote(i).volume(), remoteBarSeries.getQuote(i).volume());
    		assertEquals(barSeries.getQuote(i).interval(), remoteBarSeries.getQuote(i).interval());
    	}
    }
}
