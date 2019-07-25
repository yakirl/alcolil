package org.yakirl.alcolil.database;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;

import org.python.util.PythonInterpreter;
import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.database.Conf;
import org.yakirl.alcolil.database.FileSystemDBManager;
import org.yakirl.alcolil.database.MarketStoreDBManager;
import org.yakirl.alcolil.unittests.SuperTestCase;
// import org.black.ninia.Jep;
import org.python.core.*;

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
	public void testJepSanity() throws Exception {
		inner();
		
		Thread t = new Thread() {
			public void run() {
				inner();
			}
		};
		//t.start();
		//t.join();
		
		inner();
	}
	
    private void inner() {
    	try {
    		jep.Jep _jep;
	    	_jep = new jep.Jep(new jep.JepConfig().addSharedModules("numpy", "pandas"));
	    	// jep.Jep _jep = new jep.Jep();
			_jep.set("srcDir", conf.srcDir());
			_jep.eval("import sys");
	        _jep.eval("sys.path.append(srcDir)");
			//_jep.eval("from pymodules import test");
			_jep.eval("from pymodules import db_client");
			_jep.eval("client = db_client.MarketStoreClient()");
			_jep.invoke("client.test_connection");
			_jep.close();
    	} catch(Exception e) {
    		LOG.error("failed", e);
    	}
    }
    
  //   @Test
    public void testReadWriteTimeSeriesToQuoteDB() throws Exception {
    	//TimeSeries intcTimeSeries = new TimeSeries("INTC");
    	Quote quote;
    	String symbol = "AMD";
    	quote = new Quote(symbol, 1.02, 3.60, 0.60, 1.43, 20500, Interval.ONE_MIN, new Time(646721399L));
    	BarSeries barSeries = new BarSeries(symbol, Interval.ONE_MIN);
    	barSeries.addQuote(quote);
    	dbManager.appendToQuoteDB(barSeries);
    	
    	BarSeries intcOneMin = dbManager.readFromQuoteDB(symbol, Interval.ONE_MIN);
        // BarSeries intcOneMin = intcTimeSeriesInput.getBarSeries(Interval.ONE_MIN);
        assertEqualsDbl(1.02, intcOneMin.getQuote(0).open().doubleValue());
        assertEqualsDbl(3.60, intcOneMin.getQuote(0).high().doubleValue());
        assertEqualsDbl(0.60, intcOneMin.getQuote(0).low().doubleValue());
        assertEqualsDbl(1.43, intcOneMin.getQuote(0).close().doubleValue());
        assertEquals(20500, intcOneMin.getQuote(0).volume());
        assertEquals(Interval.ONE_MIN, intcOneMin.getQuote(0).interval());
    }
}
