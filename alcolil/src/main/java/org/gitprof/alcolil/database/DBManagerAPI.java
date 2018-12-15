package org.gitprof.alcolil.database;

import java.io.IOException;
import java.util.List;

import org.gitprof.alcolil.common.AInterval;
import org.gitprof.alcolil.common.AStockCollection;
import org.gitprof.alcolil.common.AStockSeries;
import org.gitprof.alcolil.common.ATime;
import org.gitprof.alcolil.common.ATimeSeries;


/***********************
 * @author Yakir
 * 
 *  DBManager provides high level database operations
 */

public interface DBManagerAPI {

	void validateDBStructure() throws Exception;
		
	void createDBStructure() throws Exception;
	
	AStockCollection getStockCollection() throws IOException; 
	
	void setStockCollection(AStockCollection stocks) throws IOException;

	AStockSeries readFromQuoteDB(List<String> symbols, AInterval interval) throws IOException;

	AStockSeries readFromQuoteDB(List<String> symbols) throws IOException;

	AStockSeries readFromQuoteDB(List<String> symbols, AInterval interval, ATime from, ATime to) throws IOException;

	void rewriteToQuoteDB(AStockSeries stockSeries) throws IOException;

	void appendToQuoteDB(AStockSeries stockSeries) throws IOException;

	ATimeSeries readFromQuoteDB(String symbol) throws IOException;

	ATimeSeries readFromQuoteDB(String symbol, AInterval interval, ATime from, ATime to) throws IOException;

	void rewriteToQuoteDB(ATimeSeries timeSeries) throws IOException;

	void appendToQuoteDB(ATimeSeries timeSeries) throws IOException;

}