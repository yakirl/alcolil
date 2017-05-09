package org.gitprof.alcolil.database;

import java.math.BigDecimal;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.core.Core;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.global.Conf;

import org.gitprof.alcolil.tests.SuperTestCase;

/**
 * Unit test for simple App.
 */
public class DBManagerTest extends SuperTestCase {

	protected static final Logger LOG = LogManager.getLogger(Core.class);
	private DBManager dbManager;
	private static final String STOCK_LIST_FILE_EXAMPLE = Conf.appendToStockDB("stocks_list_example.csv");
	
	public DBManagerTest(String testName)  {
        super(testName);
        dbManager = DBManager.getInstance();
    }
    
    protected void setUp() throws Exception {
    	LOG.info("setUp test");    	
    	DBManager.validateDBStructure();
    	(new File(Paths.get(Conf.rootDir, "tmp").toString())).mkdirs();
    }
    
    protected void tearDown() {
    	LOG.info("tearDown test");
    	(new File(Paths.get(Conf.rootDir, "tmp").toString())).delete();
    }

    /*
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( DBManagerTest.class );
    }
    
    
    
    public void testGetStockCollection() throws Exception {
        AStockCollection stocks = dbManager.getStockCollection(STOCK_LIST_FILE_EXAMPLE);
        AStock goog = stocks.getStock("GOOG_EXAMPLE");
        assertEquals(42.9, goog.getMarketCap().doubleValue());
        assertEquals(7.2, goog.getLastPrice().doubleValue());
        assertEquals("Technology", goog.getSector());
        AStock msft = stocks.getStock("MSFT_EXAMPLE");
        assertEquals(701.78, msft.getMarketCap().doubleValue());
        assertEquals(10.75, msft.getLastPrice().doubleValue());
        assertEquals("Technology", msft.getSector());
    }
    
    public void testSetStockCollection() throws Exception {
        String tmpFile = Paths.get(Conf.rootDir, "tmp_test_stock_collection", "stockList.csv").toString();
        
    }

    public void testReadFromQuoteDBTimeSeries() throws Exception {
        ATimeSeries googTimeSeries = dbManager.readFromQuoteDB("GOOG_EXAMPLE");
        ABarSeries googOneMin = googTimeSeries.getBarSeries(AInterval.ONE_MIN);
        assertEquals(1.02, googOneMin.getQuote(0).open().doubleValue());
        assertEquals(1.43, googOneMin.getQuote(1).close().doubleValue());
        assertEquals(3.60, googOneMin.getQuote(2).high().doubleValue());
        assertEquals(20500, googOneMin.getQuote(1).volume());
        assertEquals(AInterval.ONE_MIN, googOneMin.getQuote(1).interval());
    }
    
    public void testWriteToQuoteDBTimeSeries() throws Exception {
        
    }
    
    public void testReadFromQuoteDBStockSeries() throws Exception {
    	ArrayList<String> symbols = new ArrayList<String>();
    	symbols.add("GOOG_EXAMPLE");
    	symbols.add("MSFT_EXAMPLE");    
        // AStockSeries stockSeries = dbManager.readFromQuoteDB(symbols, AInterval.ONE_MIN);
        // assertTrue(AInterval.ONE_MIN == stockSeries.getInterval());
    }
    
    public void testWriteToQuoteDBStockSeries() throws Exception {
    
    }
}
