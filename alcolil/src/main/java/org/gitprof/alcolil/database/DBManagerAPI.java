package org.gitprof.alcolil.database;

import java.io.IOException;
import java.util.List;

import org.gitprof.alcolil.common.Interval;
import org.gitprof.alcolil.common.StockCollection;
import org.gitprof.alcolil.common.StockSeries;
import org.gitprof.alcolil.common.Time;
import org.gitprof.alcolil.common.TimeSeries;


/***********************
 * @author Yakir
 * 
 *  DBManager provides high level database operations
 */

public interface DBManagerAPI {

	void validateDBStructure() throws Exception;
		
	void createDBStructure() throws Exception;
	
	StockCollection getStockCollection() throws IOException; 
	
	void setStockCollection(StockCollection stocks) throws IOException;

	StockSeries readFromQuoteDB(List<String> symbols, Interval interval) throws IOException;

	void rewriteToQuoteDB(StockSeries stockSeries) throws IOException;

	void appendToQuoteDB(StockSeries stockSeries) throws IOException;

	TimeSeries readFromQuoteDB(String symbol) throws IOException;

	void rewriteToQuoteDB(TimeSeries timeSeries) throws IOException;

	void appendToQuoteDB(TimeSeries timeSeries) throws IOException;

}