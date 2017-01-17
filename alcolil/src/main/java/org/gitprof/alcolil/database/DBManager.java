package org.gitprof.alcolil.database;

import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.global.Conf;

public class DBManager {

	Map<DBSection, Lock> sectionLocks;
	Path QuoteDB;
	Path TradeDB;
	Path StockDB;
	Path AlarmDB;
	
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
	
	
	public void writeToQuoteDB(ATimeSeries timeSeries) {
		String symbol = timeSeries.getSymbol();
		Path dirPath = Paths.get(Conf.quoteDB + "\\" + symbol);
		createDir(dirPath);
		
		
	}
	
	public void createFile(Path path) {
		
	}
	
	public void deleteFile(Path path) {
		
	}
	
	public void writeToFile(Path path) {
		
	}
	
	
	public Path getFullPath() {
		return null;
	}
	
	
	
	
	
	
}
