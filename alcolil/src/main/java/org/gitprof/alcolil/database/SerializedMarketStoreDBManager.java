package org.gitprof.alcolil.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gitprof.alcolil.common.*;

/********
 * 
 * @author yakir
 *
 * This class should solve the issue of multiple Jep sub interpreters which make use of CPython libs like numpy and pandas.
 * Those modules defines C static variable, which are shared among all sub-interpreters, this causes to variaty of problems.
 * Due to lack of another appropriate solution I decide for now to serialize all DB access via one worke thread that will
 * 	be the only one use Jep.  
 */

public class SerializedMarketStoreDBManager implements DBManagerAPI {
	private static final String DB_CONN_STRING_ENV = "DB_CONN_STRING";
	protected static final Logger LOG = LogManager.getLogger(MarketStoreDBManager.class);
	private static DBManagerAPI dbManager = null;
	protected static Conf conf;
	private static jep.Jep _jep;
	private static Thread workerThread;
	private BlockingQueue<Job> workQueue;
	private static AtomicBoolean stop;
	private long waitOnQueueMillis;
	private long jobTimeoutMillis;
	
	
	public SerializedMarketStoreDBManager() {
		String dbConnString = System.getenv(DB_CONN_STRING_ENV);
		if (dbConnString != null) {
			LOG.debug("db conn string set to " + dbConnString);
			conf = new Conf(dbConnString);	
		} else {
			LOG.debug("db conn string isnt set, using default");
			conf = new Conf();
		}
	}
	
	public static synchronized DBManagerAPI getInstance() {
		if (dbManager == null) {
			LOG.info("Initializing MarkStore DB Manager");
			dbManager = new SerializedMarketStoreDBManager();
		}
		return dbManager;	
	}
	
	public static synchronized void spawnDBWorker() {
		if (workerThread != null) {
			LOG.warn("DB worker thread is already running!");
			return;
		}
		workerThread = new Thread() {
			public void run() {				
				getInstance();
				try {
					initJep();
				} catch(jep.JepException e) {
					LOG.error("failed to initialize jep session. DB worker shutting down...", e);
					return;
				}
				((SerializedMarketStoreDBManager)dbManager).mainLoop();
				
			}
		};
		stop.set(false);
		workerThread.start();
	}
	
	public void close() throws InterruptedException {
		stopDBWorker();
		workerThread.join();
	}
	
	public static void stopDBWorker() {
		stop.set(true);
	}
	
	private static jep.Jep initJep() throws jep.JepException {
		LOG.info("initJep: Thread=" + Thread.currentThread().getId());
		_jep = new jep.Jep(new jep.JepConfig().addSharedModules("numpy", "pandas"));
		_jep.eval("import sys");
        _jep.set("srcDir", conf.srcDir());
        _jep.eval("sys.path.append(srcDir)");
        _jep.eval("from pymodules import db_client");
        _jep.eval("client = db_client.MarketStoreClient()");
        return _jep;
	}
	
	private abstract class Job {
		public Object ret;
		public jep.JepException exc;
		
		public abstract void execute();
	}
	
	private void mainLoop() {
		while(stop.get() == false) {
			try {
				Job job = workQueue.poll(waitOnQueueMillis, TimeUnit.MILLISECONDS);
				job.execute();
				job.notify();
			} catch (InterruptedException e) {
				
			}
		}
		try {
			_jep.close();
		} catch(jep.JepException e) {
			LOG.error("failed to close jep session", e);
		}
	}
	
	public static void verifyDBWorkerRunning() throws DBException {
		if (workerThread == null) {
			throw new DBException("DB worker is not running!");
		}
	}
	
	public void validateDBStructure() throws Exception {
		Job j = new Job() {
			public void execute() {
				try {
					_jep.invoke("client.test_connection");
				} catch (jep.JepException e) {
					exc = e;
				}
			}
		};
		sendToQueue(j);
		
	}
	
	public StockCollection getStockCollection() throws IOException {
		StockCollection stocks = new StockCollection();
		return stocks;
	}
	
	public void setStockCollection(StockCollection stocks) throws IOException {
		
	}

	public StockSeries readFromQuoteDB(List<String> symbols, Interval interval) throws DBException {
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

	public void appendToQuoteDB(StockSeries stockSeries) throws Exception {
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
	
	private void sendToQueue(Job j) throws DBException {
		try {
			workQueue.put(j);
			j.wait(jobTimeoutMillis);
		} catch (InterruptedException e) {
			throw new DBException("failed to send job to DB worker", e);
		}	
	}
	
	public BarSeries readFromQuoteDB(String symbol, Interval interval) throws DBException {
		Job j = new Job() {
			public void execute() {
				try {
					ret = _jep.invoke("client.read_from_quote_db", symbol, interval.name());
				} catch (jep.JepException e) {
					exc = e;
				}
			}
		};
		sendToQueue(j);
        return convertPyResToBarSeries(symbol, interval, j.ret);
	}
	
	public void rewriteToQuoteDB(BarSeries barSeries) throws jep.JepException {
		// TODO: implement when destroy API of marketstore is ready
	}
	
	public void appendToQuoteDB(BarSeries barSeries) throws DBException {
		LOG.info("appending to quote DB. num quotes =" + barSeries.size());
		Job j = new Job() {
			public void execute() {
				try {
					_jep.set("obj", barSeries);
			        _jep.eval("client.write_to_quote_db(obj)");
				} catch (jep.JepException e) {
					exc = e;
				}
			}
		};
		sendToQueue(j);
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