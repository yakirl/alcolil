package org.gitprof.alcolil.database;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.FileSystemDBManager;
import org.gitprof.alcolil.database.Conf;
import org.gitprof.alcolil.unittests.SuperTestCase;

/**
 * Unit test for DBManager.
 * 
 * A real filesystem is used for the UT
 */

public class FileSystemDBManagerTest extends SuperTestCase {

	protected static final Logger LOG = LogManager.getLogger(FileSystemDBManagerTest.class);
	private DBManagerAPI dbManager;
	
	public FileSystemDBManagerTest()  {
        super();
        dbManager = FileSystemDBManager.getInstance();
    }
    
	@Before
    public void setUp() throws Exception {
    	LOG.info("setUp test");
    	dbManager.createDBStructure();
    }
    
	@After
    public void tearDown() {
    	LOG.info("tearDown test");
    }   
        
    @Test
    public void testSetGetStockCollection() throws Exception {
    	LOG.info("run testSetGetStockCollection");
    	AStockCollection stocks = new AStockCollection();
 
    	// Symbol, sector, marketCap, lastPrice, avgVolOfXDays
    	AStock stock = new AStock();
    	stock.initFromCSV(new String[] {"GOOG", "Technology", "3.4", "230", "10.2" });
    	stocks.add(stock);
    	stock = new AStock();
    	stock.initFromCSV(new String[] {"MSFT", "Technology", "8.4", "130", "6.2" });
    	stocks.add(stock);
    	stock = new AStock();
    	stock.initFromCSV(new String[] {"BAK", "Finance", "1.4", "210", "12.3" });
    	stocks.add(stock);
    	stock = new AStock();
    	stock.initFromCSV(new String[] {"AMD", "Technology", "2.2", "230", "34.2" });
    	stocks.add(stock);
    	dbManager.setStockCollection(stocks);
    	
        stocks = dbManager.getStockCollection();
        AStock goog = stocks.getStock("GOOG");
        assertEqualsDbl(3.4, goog.getMarketCap().doubleValue());
        assertEqualsDbl(230, goog.getLastPrice().doubleValue());
        assertEquals("Technology", goog.getSector());
        AStock msft = stocks.getStock("MSFT");
        assertEqualsDbl(8.4, msft.getMarketCap().doubleValue());
        assertEqualsDbl(130, msft.getLastPrice().doubleValue());
        assertEquals("Technology", msft.getSector());
    }

    @Test
    public void testReadWriteTimeSeriesToQuoteDB() throws Exception {
    	ATimeSeries intcTimeSeries = new ATimeSeries("INTC");
    	AQuote quote;
    	quote = new AQuote("INTC", 1.02, 3.60, 0.60, 1.43, 20500, AInterval.ONE_MIN, new ATime(3202320));
    	intcTimeSeries.addQuote(quote);
    	dbManager.rewriteToQuoteDB(intcTimeSeries);
    	
        ATimeSeries intcTimeSeriesInput = dbManager.readFromQuoteDB("INTC");
        ABarSeries intcOneMin = intcTimeSeriesInput.getBarSeries(AInterval.ONE_MIN);
        assertEqualsDbl(1.02, intcOneMin.getQuote(0).open().doubleValue());
        assertEqualsDbl(3.60, intcOneMin.getQuote(0).high().doubleValue());
        assertEqualsDbl(0.60, intcOneMin.getQuote(0).low().doubleValue());
        assertEqualsDbl(1.43, intcOneMin.getQuote(0).close().doubleValue());
        assertEquals(20500, intcOneMin.getQuote(0).volume());
        assertEquals(AInterval.ONE_MIN, intcOneMin.getQuote(1).interval());
    }
    
    public void testReadWriteStockSeriesToQuoteDB() throws Exception {
    	ArrayList<String> symbols = new ArrayList<String>();
    	symbols.add("GOOG_EXAMPLE");
    	symbols.add("MSFT_EXAMPLE");    
        // AStockSeries stockSeries = dbManager.readFromQuoteDB(symbols, AInterval.ONE_MIN);
        // assertTrue(AInterval.ONE_MIN == stockSeries.getInterval());
    }
    
    public void testWriteToQuoteDBStockSeries() throws Exception {
    
    }
}
