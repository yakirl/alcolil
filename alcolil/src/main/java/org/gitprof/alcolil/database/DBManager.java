package org.gitprof.alcolil.database;

import java.nio.file.Path;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class DBManager {

	Path QuoteDB;
	Path TradeDB;
	Path StockDB;
	Path AlarmDB;
	
	private DBManager dbManager = null;
	
	private DBManager() {
		
	}
	
	public DBManager getInstance() {
		if (null == dbManager) 
			dbManager = new DBManager();
		return dbManager;	
	}
	
	private enum Op {
		CREATE, DELETE
	}
	
	public void createDir(Path path) {
		createDeleteDir(path, Op.CREATE);
	}
	
	public void deleteDir(Path path) {
		createDeleteDir(path, Op.DELETE);
	}
	
	private synchronized void createDeleteDir(Path path, Op op) {
		File folder = new File(path.toString());
		folder.mkdirs();
	}
	
	public void createFile(Path path) {
		
	}
	
	public void deleteFile(Path path) {
		
	}
	
	public void writeToFile(Path path) {
		
	}
	
	
	
	
	
	
	
	
}
