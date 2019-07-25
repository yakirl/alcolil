package org.gitprof.alcolil.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;


import java.util.Random;
import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.unittests.SuperTestCase;


/**
 * Unit test for DBManager.
 * 
 * A real marketstore instance is used for the UT
 */

public class SerializedMarketStoreDBManagerTest extends SuperTestCase {

	protected static final Logger LOG = LogManager.getLogger(FileSystemDBManagerTest.class);
	
	public SerializedMarketStoreDBManagerTest() throws Exception {
        super();
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

    @Test(expected = DBException.class)
    public void testNoThreadRunning() throws DBException {
    	SerializedMarketStoreDBManager dbManager = (SerializedMarketStoreDBManager) SerializedMarketStoreDBManager.getInstance();
    	dbManager.validateDBStructure();
    }
    
    @Test
    public void testSerialAccess() throws Exception {
    	SerializedMarketStoreDBManager dbManager = (SerializedMarketStoreDBManager) SerializedMarketStoreDBManager.getInstance();
    	SerializedMarketStoreDBManager.spawnDBWorker();
    	dbManager.validateDBStructure();
        writeReadRandomQuote(dbManager, "GOOG");
        writeReadRandomQuote(dbManager, "AAPL");
        dbManager.close();
    }
    
    @Test
    public void testConcurrentAccess() throws Exception {
    	SerializedMarketStoreDBManager dbManager = (SerializedMarketStoreDBManager) SerializedMarketStoreDBManager.getInstance();
    	SerializedMarketStoreDBManager.spawnDBWorker();
    	LOG.info("validating");
    	dbManager.validateDBStructure();
    	Thread threads[] = new Thread[5];
    	Random rand = new Random();
    	final Exception exc = new Exception();
    	LOG.info("spawning");
    	for (int i = 0; i < 5; i++) {
    		threads[i] = new Thread() {
    			public void run() {
    				try {
    					Thread.sleep(rand.nextInt(1000));
    					LOG.error("this is thread...");
    			        writeReadRandomQuote(dbManager, "GOOG");
    			        writeReadRandomQuote(dbManager, "AAPL");    					
    				} catch(Exception e) {
    					exc.addSuppressed(e);
    					
    				}
    			}
    		};
    		threads[i].start();
    	}
    	LOG.info("finished spawning");
    	if (exc.getSuppressed().length != 0) {
    		throw exc;
    	}
    	for (int i = 0; i < 5; i++) {
    		threads[i].join();
    	}
    	LOG.info("finished joining");
        dbManager.close();
    }
    
    public void writeReadRandomQuote(SerializedMarketStoreDBManager dbManager, String symbol) throws DBException {
    	Quote quote;
    	Random rand = new Random();
    	double open = rand.nextDouble();
    	double high = rand.nextDouble();
    	double low = rand.nextDouble();
    	double close = rand.nextDouble();
    	long volume = rand.nextLong();
    	quote = new Quote(symbol, open, high, low, close, volume, Interval.ONE_MIN, new Time(646721399L));
    	BarSeries barSeries = new BarSeries(symbol, Interval.ONE_MIN);
    	barSeries.addQuote(quote);
    	dbManager.appendToQuoteDB(barSeries);
    	
    	BarSeries oneMinBar = dbManager.readFromQuoteDB(symbol, Interval.ONE_MIN);
        // BarSeries intcOneMin = intcTimeSeriesInput.getBarSeries(Interval.ONE_MIN);
        assertEqualsDbl(open, oneMinBar.getQuote(0).open().doubleValue());
        assertEqualsDbl(high, oneMinBar.getQuote(0).high().doubleValue());
        assertEqualsDbl(low, oneMinBar.getQuote(0).low().doubleValue());
        assertEqualsDbl(close, oneMinBar.getQuote(0).close().doubleValue());
        assertEquals(volume, oneMinBar.getQuote(0).volume());
        assertEquals(Interval.ONE_MIN, oneMinBar.getQuote(0).interval());
    }
}
