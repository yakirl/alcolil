package org.yakirl.alcolil.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yakirl.alcolil.common.*;

/********
 * 
 * @author yakir
 *
 * This class make use of Jep in order to invoke the python client of marketstore.
 * Jep uses python sub interpreter. Every Jep instantiation uses different interpreter, and should be used only by the same thread, and be 
 * 	closed when finished.
 * To solve this, we instantiate new jep object every operation. this method is bad in aspect of performance, and other methods should be considered in future
 * 	such as saving mapping: ThreadID -> JepInstance, which also require to handle closure of those jep instances, when this DBManager object no longer used. 
 *  
 * The synchronized access to the DB itself - marketstore - should handled internally (I should check this is the case actually :)) 
 */

public class MarketStoreDBManager implements DBManagerAPI {
	private static final String DB_CONN_STRING_ENV = "DB_CONN_STRING";
	protected static final Logger LOG = LogManager.getLogger(MarketStoreDBManager.class);
	protected Conf conf;
	// private jep.Jep jep;
	
	public MarketStoreDBManager() {
		String dbConnString = System.getenv(DB_CONN_STRING_ENV);
		if (dbConnString != null) {
			LOG.debug("db conn string set to " + dbConnString);
			conf = new Conf(dbConnString);	
		} else {
			LOG.debug("db conn string isnt set, using default");
			conf = new Conf();
		}		
	}
	
	private jep.Jep initJep() throws jep.JepException {
		jep.Jep _jep = new jep.Jep(new jep.JepConfig().addSharedModules("numpy", "pandas"));
		// jep.Jep _jep =  new jep.Jep();
		LOG.info("initJep: Thread=" + Thread.currentThread().getId());
		// jep.
		_jep.eval("import sys");
        _jep.set("srcDir", conf.srcDir());
        _jep.eval("sys.path.append(srcDir)");
        _jep.eval("from pymodules import db_client");
        _jep.eval("client = db_client.MarketStoreClient()");
        return _jep;
	}
	
	public static synchronized DBManagerAPI getInstance() throws jep.JepException {
		LOG.info("Initializing MarkStore DB Manager");
		DBManagerAPI dbManager = new MarketStoreDBManager();
		return dbManager;	
	}
	
	public void validateDBStructure() throws Exception {
		jep.Jep _jep = initJep();
		_jep.invoke("client.test_connection");
		_jep.close();
	}
	
	public void close() throws jep.JepException {
		// jep.close();
	}
	
	public StockCollection getStockCollection() throws IOException {
		StockCollection stocks = new StockCollection();
		return stocks;
	}
	
	public void setStockCollection(StockCollection stocks) throws IOException {
		
	}

	public StockSeries readFromQuoteDB(List<String> symbols, Interval interval) throws jep.JepException {
		StockSeries stockSeries = new StockSeries(Interval.ONE_MIN);
		BarSeries barSeries;
		for (String symbol: symbols) {
			barSeries = readFromQuoteDB(symbol, interval);
			stockSeries.addBarSeries(symbol, barSeries);
		}
		return stockSeries;
	}

	public void rewriteToQuoteDB(StockSeries stockSeries) throws Exception {
		
	}

	public void appendToQuoteDB(StockSeries stockSeries) throws jep.JepException {
		for (String symbol: stockSeries.getSymbolList()) {
			appendToQuoteDB(stockSeries.getBarSeries(symbol));
		}
	}
	
	public TimeSeries readFromQuoteDB(String symbol) throws IOException {
		TimeSeries timeSeries = new TimeSeries(symbol);
		return timeSeries;
	}
    
	public void rewriteToQuoteDB(TimeSeries timeSeries) throws IOException {
		
	}

	public void appendToQuoteDB(TimeSeries timeSeries) throws IOException {
		
	}
	
	public BarSeries readFromQuoteDB(String symbol, Interval interval) throws jep.JepException {
		jep.Jep jep = initJep();
        Object res = jep.invoke("client.read_from_quote_db", symbol, interval.name());
        jep.close();
        return convertPyResToBarSeries(symbol, interval, res);
	}
	
	public void rewriteToQuoteDB(BarSeries barSeries) throws jep.JepException {
		// TODO: implement when destroy API of marketstore is ready
	}
	
	public void appendToQuoteDB(BarSeries barSeries) throws jep.JepException { 
		LOG.info("appending to quote DB. num quotes =" + barSeries.size());
		jep.Jep jep = initJep();
        jep.set("obj", barSeries);
        jep.eval("client.write_to_quote_db(obj)");
        jep.close();
	}
	
	private BarSeries convertPyResToBarSeries(String symbol, Interval interval, Object pyBarSeries) {
		Integer x = new Integer(8);
		// long y = x.longValue();
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
}