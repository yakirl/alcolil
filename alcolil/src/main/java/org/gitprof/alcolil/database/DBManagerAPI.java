package org.gitprof.alcolil.database;

import java.io.IOException;
import java.util.List;

import org.gitprof.alcolil.common.*;


/***********************
 * @author Yakir
 * 
 *  DBManager provides high level database operations
 *  TODO: split to interfaces per DB
 *  TODO: currently most FSDBManager methods throw IOException and most MSDBManager methods throws jep.JepExcetion
 *  	we should convert both to DBException. to make it unique and easy to understand the error on higher levels modules
 */

public interface DBManagerAPI {

	/*** General Operations **/
	
	void validateDBStructure() throws Exception;
	
	void close() throws Exception;
	
	/*** Stocks DB ***/
	
	StockCollection getStockCollection() throws IOException; 
	
	void setStockCollection(StockCollection stocks) throws IOException;

	/*** Quotes DB ***/
	
	StockSeries readFromQuoteDB(List<String> symbols, Interval interval) throws Exception;

	void rewriteToQuoteDB(StockSeries stockSeries) throws Exception;

	void appendToQuoteDB(StockSeries stockSeries) throws Exception;

	TimeSeries readFromQuoteDB(String symbol) throws Exception;

	void rewriteToQuoteDB(TimeSeries timeSeries) throws Exception;

	void appendToQuoteDB(TimeSeries timeSeries) throws Exception;
	
	BarSeries readFromQuoteDB(String symbol, Interval interval) throws Exception;

	void rewriteToQuoteDB(BarSeries barSeries) throws Exception;

	void appendToQuoteDB(BarSeries barSeries) throws Exception;
}