package org.gitprof.alcolil.database;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Iterable;
import java.lang.reflect.*;
// import java.util.Collections.UnmodifiableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gitprof.alcolil.common.*;

/********
 * 
 * @author yakir
 *
 * This class make use of Jep in order to invoke the python client of marketstore.
 * Jep uses python sub interpreter. Every Jep instantiation uses different interpreter, and should be used only by the same thread, and be 
 * 	closed when finished.
 * Thus we instantiate new object for every db usage.
 * The synchronized access to the DB itself - marketstore - should handled internally (I should check this is the case actually :)) 
 */

public class MarketStoreDBManager implements DBManagerAPI {
	private static final String DB_CONN_STRING_ENV = "DB_CONN_STRING";
	protected static final Logger LOG = LogManager.getLogger(MarketStoreDBManager.class);
	protected Conf conf;
	private jep.Jep jep;
	
	public MarketStoreDBManager() throws jep.JepException {
		String dbConnString = System.getenv(DB_CONN_STRING_ENV);
		if (dbConnString != null) {
			LOG.debug("db conn string set to " + dbConnString);
			conf = new Conf(dbConnString);	
		} else {
			LOG.debug("db conn string isnt set, using default");
			conf = new Conf();
		}
	}
	
	public static synchronized DBManagerAPI getInstance() throws jep.JepException {
		LOG.info("Initializing MarkStore DB Manager");
		DBManagerAPI dbManager = new MarketStoreDBManager();
		return dbManager;	
	}
	
	public void validateDBStructure() throws Exception {
		
	}
	
	public void createDBStructure() throws Exception {
		initJep();
	}
	
	public void close() throws jep.JepException {
		jep.close();
	}
	
	public StockCollection getStockCollection() throws IOException {
		StockCollection stocks = new StockCollection();
		return stocks;
	}
	
	public void setStockCollection(StockCollection stocks) throws IOException {
		
	}

	public StockSeries readFromQuoteDB(List<String> symbols, Interval interval) throws IOException {
		StockSeries stockSeries = new StockSeries(Interval.ONE_MIN);
		return stockSeries;
	}

	public void rewriteToQuoteDB(StockSeries stockSeries) throws IOException {
		
	}

	public void appendToQuoteDB(StockSeries stockSeries) throws IOException {
		
	}

	public void appendToQuoteDB(TimeSeries timeSeries) throws IOException {
		
	}
	
	public TimeSeries readFromQuoteDB(String symbol) throws IOException {
		TimeSeries timeSeries = new TimeSeries(symbol);
		return timeSeries;
	}
    
	public void rewriteToQuoteDB(TimeSeries timeSeries) throws IOException {
		
	}
	
	private void initJep() throws jep.JepException {
		jep = new jep.Jep();
		jep.eval("import sys");
        jep.set("srcDir", conf.srcDir());
        jep.eval("sys.path.append(srcDir)");
        jep.eval("from pymodules import db_client");
        jep.eval("client = db_client.MarketStoreClient()");
	}
	
	public void writeToQuoteDB(BarSeries barSeries) throws jep.JepException {    	
        jep.set("obj", barSeries);
        jep.eval("client.write_to_quote_db(obj)");        
	}
	
	private BarSeries convertPyResToBarSeries(String symbol, Interval interval, Object pyBarSeries) {
		Integer x = new Integer(8);
		long y = x.longValue();
		BarSeries barSeries = new BarSeries(symbol, interval);
		List<Object> quoteRaw;
		for (Object obj: (ArrayList<Object>) pyBarSeries) {
        	quoteRaw = (List<Object>) obj;        
        	Quote quote = new Quote(symbol,
        						    ((Double)quoteRaw.get(1)).doubleValue(),
        						    ((Double)quoteRaw.get(2)).doubleValue(),
        						    ((Double)quoteRaw.get(3)).doubleValue(),
        						    ((Double)quoteRaw.get(4)).doubleValue(),        						    
        						    ((Integer)quoteRaw.get(5)).longValue(),
        						    interval,
        						    new Time(((Integer)quoteRaw.get(0)).longValue()));
        	barSeries.addQuote(quote);
        }
		return barSeries;
	}
	
	public BarSeries readFromQuoteDB(String symbol, Interval interval) throws jep.JepException {
		System.out.println("read from quote DB bar series");        
        Object res = jep.invoke("client.read_from_quote_db", symbol, interval.name());                                       
        return convertPyResToBarSeries(symbol, interval, res);
	}
}