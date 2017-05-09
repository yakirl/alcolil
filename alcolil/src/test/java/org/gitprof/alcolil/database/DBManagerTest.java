package org.gitprof.alcolil.database;
 
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.core.Core;
import org.gitprof.alcolil.database.DBManager;

/**
 * Unit test for simple App.
 */
public class DBManagerTest extends TestCase {

	protected static final Logger LOG = LogManager.getLogger(Core.class);
	private DBManager dbManager;
	
	public DBManagerTest(String testName)  {
        super(testName);
        dbManager = DBManager.getInstance();
    }
    
    protected void setUp() {
    	LOG.info("setUp test");
    }
    
    protected void tearDown() {
    	LOG.info("tearDown test");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DBManagerTest.class );
    }

    public void testReadFromQuoteDBStockSeries() throws Exception {
    	ArrayList<String> symbols = new ArrayList<String>();
    	symbols.add("GOOG");
    	symbols.add("MSFT");
    	String file1 = System.getProperty("my.log");
	    String file2 = System.getProperty("log4j.configurationFile");
        System.out.println( "Executer start " + file1 + file2);
        // AStockSeries stockSeries = dbManager.readFromQuoteDB(symbols, AInterval.ONE_MIN);
        // assertTrue(AInterval.ONE_MIN == stockSeries.getInterval());
    }
}
