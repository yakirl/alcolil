package org.gitprof.alcolil.database;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.global.Conf;

public class DBManager {

	Map<DBSection, Lock> sectionLocks;
	Path quoteDB;
	Path tradeDB;
	Path stockDB;
	Path alarmDB;
	
	private static DBManager dbManager = null;
	
	
	private DBManager() {
		initSectionLocks();
	}
	
	public static DBManager getInstance() {
		if (null == dbManager) 
			dbManager = new DBManager();
		return dbManager;	
	}
	
	private void initSectionLocks() {
		sectionLocks.put(DBSection.QUOTE_DB, new ReentrantLock());
		sectionLocks.put(DBSection.TRADE_DB, new ReentrantLock());
		sectionLocks.put(DBSection.STOCK_DB, new ReentrantLock());
		sectionLocks.put(DBSection.STATS_DB, new ReentrantLock());
	}
	
	public enum DBSection {
		QUOTE_DB, TRADE_DB, STOCK_DB, STATS_DB
	}
	
	private enum DirOp {
		CREATE, DELETE
	}
	
	// create if not already exists
	public void createDir(Path path) {
		//createDeleteDir(path, DirOp.CREATE);
		File folder = new File(path.toString());
		folder.mkdirs();
		
	}
	
	public void deleteDir(Path path) {
		//createDeleteDir(path, DirOp.DELETE);
		File folder = new File(path.toString());
		folder.delete();
	}
	
	private synchronized void createDeleteDir(Path path, DirOp op) {
		// nothing
	}
	
	private enum FileOp {
		MODIFY, READ
	}
	
	public Path getSectionPath() {
		return null;
	}
	
	public AStockSeries readFromQuoteDB(List<String> symbols, AInterval interval) throws IOException {
		AStockSeries stockSeries = new AStockSeries(interval);
		ATimeSeries timeSeries;
		ABarSeries barSeries;
		for (String symbol : symbols) {
			timeSeries = readFromQuoteDB(symbol);
			barSeries = timeSeries.getBarSeries(interval);
			stockSeries.addBarSeries(symbol, barSeries);
		}
		return stockSeries;
	}
	
	// TODO: handle unlocking in case of excpetion
	public ATimeSeries readFromQuoteDB(String symbol) throws IOException {
		sectionLocks.get(quoteDB).lock();
		File dir = new File(Conf.quoteDB);
		File[] directoryListing = dir.listFiles();
		List<String[]> csvLines;
		ATimeSeries timeSeries = new ATimeSeries(symbol);
		for (File child : directoryListing) {
			//getQuoteFileFullPath(symbol, interval);
			//String pathname = Paths.get(Conf.quoteDB, symbol, child.getName());
		    csvLines = readFromFile(child.getPath());
		    AInterval interval = getIntervalByFilePath(child.getPath());
			ABarSeries barSeries = new ABarSeries(interval);
			for (String[] csvLine : csvLines) {
				barSeries.addQuote((AQuote)((new AQuote()).initFromCSV(csvLine)));
			}
			timeSeries.addBarSeries(interval, barSeries);
		} 
		sectionLocks.get(quoteDB).unlock();
		return null;
	}
	
	// TODO: handle unlocking in case of excpetion
	public void writeToQuoteDB(ATimeSeries timeSeries, boolean append) throws IOException {
		sectionLocks.get(quoteDB).lock();
		String symbol = timeSeries.getSymbol();
		Path dirPath = Paths.get(Conf.quoteDB + "\\" + symbol);
		createDir(dirPath);
		List<AQuote> quotes;
		List<String[]> csvLines = new ArrayList<String[]>();
		for (AInterval interval : AInterval.values()) {
			quotes = timeSeries.getBarSeries(interval).getQuotes();
			if (null == quotes) 
				continue;
			for (AQuote quote : quotes) {
				csvLines.add(quote.convertToCSV());
			}
			String pathname = getQuoteFileFullPath(symbol, interval);
			writeToFile(pathname, csvLines, append);	
		}
		sectionLocks.get(quoteDB).unlock();
	}
	
	public void rewriteToQuoteDB(ATimeSeries timeSeries) throws IOException {
		writeToQuoteDB(timeSeries, false);
	}
	
	public void appendToQuoteDB(ATimeSeries timeSeries) throws IOException {
		writeToQuoteDB(timeSeries, true);
	}
	
	public void createFile(String path) {
		
	}
	
	public void deleteFile(String path) {
		
	}
	
	public void writeToFile(String pathname, List<CSVable> csvables) {
		
	}
	
	private void writeToFile(String pathname, List<String[]> lines, boolean append) throws IOException {
		CSVWriter csvWriter = new CSVWriter(new FileWriter(pathname, append));
		csvWriter.writeAll(lines);
		csvWriter.close();
	}
	
	private List<String[]> readFromFile(String pathname) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(pathname));
		List<String[]> csvLines =  csvReader.readAll();
		csvReader.close();
		return csvLines;
		
	}
	
	public String getQuoteFileFullPath(String symbol, AInterval interval) {
		return Paths.get(Conf.quoteDB, symbol, symbol + "_" + interval.toString()).toString();  
	}
	
	private AInterval getIntervalByFilePath(String pathname) throws IOException {
		AInterval interval = null;
		if (pathname.contains(AInterval.ONE_MIN.toString())) {
			interval = AInterval.ONE_MIN;
		} else if (pathname.contains(AInterval.FIVE_MIN.toString())) {
			interval = AInterval.FIVE_MIN;
		} else if (pathname.contains(AInterval.DAILY.toString())) {
			interval = AInterval.DAILY;
		} else {
			throw new IOException();
		}
		return interval;
	}
	
	public AStockCollection getStockCollection() throws IOException {
		AStockCollection stockCollection = new AStockCollection();
		List<String[]> csvLines = readFromFile(stockDB.toString());
		for (String[] csvLine : csvLines) {
			stockCollection.add((AStock)(new AStock()).initFromCSV(csvLine));
		}
		return stockCollection;
	}
	
}
