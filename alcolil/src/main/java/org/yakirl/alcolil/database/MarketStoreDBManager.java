package org.yakirl.alcolil.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yakirl.alcolil.common.*;

import org.yakirl.marketstore.JClient;
import org.yakirl.marketstore.requests.*;
import org.yakirl.marketstore.responses.*;

/********
 * 
 * @author yakir
 * 
 * Data in MS sits in the same order of Quote: Open, High, Low, Close, Volume, Time
 *
 *  
 */

public class MarketStoreDBManager implements DBManagerAPI {
	private static final String DB_CONN_STRING_ENV = "DB_CONN_STRING";
	protected static final Logger LOG = LogManager.getLogger(MarketStoreDBManager.class);
	private JClient client;
	protected Conf conf;
	
	private Map<String, String> intervalConverter = new HashMap<String, String>() {{
		put(Interval.ONE_MIN.toString(), "1Min");		
	}};
	
	private String attrGroup;
	
	public MarketStoreDBManager() {
		String dbConnString = System.getenv(DB_CONN_STRING_ENV);
		if (dbConnString != null) {
			LOG.debug("db conn string set to " + dbConnString);
			conf = new Conf(dbConnString);	
		} else {
			LOG.debug("db conn string isnt set, using default");
			conf = new Conf();
		}
		client = new JClient();
		attrGroup = "OHLCV";
	}	
	
	public static synchronized DBManagerAPI getInstance() {
		LOG.info("Initializing MarkStore DB Manager");
		DBManagerAPI dbManager = new MarketStoreDBManager();
		return dbManager;	
	}
	
	public void validateDBStructure() throws Exception {
	
	}
	
	public void close() {
	}
	
	private String convertInterval(Interval interval) throws DBException {
		String msInterval = intervalConverter.get(interval.toString());
		if (msInterval == null) {
			throw new DBException(String.format("unsupported interval %s", interval.toString()));
		}
		return msInterval;
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

	public void appendToQuoteDB(StockSeries stockSeries) throws DBException {
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
	
	public BarSeries readFromQuoteDB(String symbol, Interval interval) throws DBException {
		try {
			String symbols[] = {symbol};
			QueryRequest req = new QueryRequest(symbols, convertInterval(Interval.ONE_MIN), attrGroup);
			QueryResponse res = client.query(req);
	        return convertMSResToBarSeries(symbol, interval, res);
		} catch (Exception e) {
			throw new DBException("failed to read from quote DB", e);
		}
	}
	
	public void rewriteToQuoteDB(BarSeries barSeries) throws DBException {
		// TODO: implement when destroy API of marketstore is ready
	}
	
	public void appendToQuoteDB(BarSeries barSeries) throws DBException { 
		LOG.info("appending to quote DB. num quotes =" + barSeries.size());
		
		// Convert BarSeries to separate arrays.
		int length = barSeries.size();
		long epochs[] = new long[length];
		double opens[] = new double[length];
		double lows[] = new double[length];
		double highs[] = new double[length];
		double closes[] = new double[length];
		long volumes[] = new long[length];

		int i = 0; for (Quote quote: barSeries) {
			epochs[i] = quote.time().getSeconds();
			opens[i] = quote.open().floatValue();
			highs[i] = quote.high().floatValue();
			lows[i] = quote.low().floatValue();
			closes[i] = quote.close().floatValue();
			volumes[i] = quote.volume();		
		}
		//Object[] data = new Object[] {epochs, opens, };
		
		try {
			WriteRequest req = new WriteRequest(barSeries.getSymbol(), "1Min", attrGroup, barSeries.size());
			req.addDataColum("Epoch", epochs);
			req.addDataColum("Open", opens);
			req.addDataColum("High", highs);
			req.addDataColum("Low", lows);
			req.addDataColum("Close", closes);
			req.addDataColum("Volume", volumes);
			client.write(req);
		} catch (Exception e) {
			throw new DBException("marketstore write request failed", e);
		}
	}
	
	private BarSeries convertMSResToBarSeries(String symbol, Interval interval, QueryResponse res) {		
		BarSeries barSeries = new BarSeries(symbol, interval);
		int length = ((long[])res.data()[0]).length;
		int i; for (i = 0; i < length; i++) {
        	Quote quote = new Quote(symbol,
        						    ((double[])res.data()[1])[i],
        						    ((double[])res.data()[2])[i],
        						    ((double[])res.data()[3])[i],
        						    ((double[])res.data()[4])[i],        						    
        						    ((long[])res.data()[5])[i],
        						    interval,
        						    new Time(((long[])res.data()[0])[i]));
        	barSeries.addQuote(quote);
        }
		return barSeries;
	}
}