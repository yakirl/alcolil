package org.yakirl.alcolil.database;

import java.util.Map;

import java.util.EnumMap;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.reflect.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.apache.logging.log4j.Logger;
import org.yakirl.alcolil.common.*;
import org.apache.logging.log4j.LogManager;

/***********************
 * @author Yakir
 * 
 *  DBManager provides high level database operations
 *  Usage:
 *  this class should be used as singleton object with concurrent usage among multiple java threads
 *  access to files is synchronized using lock for every database (locks the entire db during the operation)
 *
 */

public class FileSystemDBManager implements DBManagerAPI {

	private static final String DB_CONN_STRING_ENV = "DB_CONN_STRING";
    protected static final Logger LOG = LogManager.getLogger(FileSystemDBManager.class);
	Map<DBSection, Lock> sectionLocks;	
	private static DBManagerAPI dbManager = null;
	private static int lockCount = 0;  // for debug
	protected Conf conf;
	private String tmpQuotesSingleFile = "1.csv";
	
	private FileSystemDBManager() {
		String dbConnString = System.getenv(DB_CONN_STRING_ENV);
		if (dbConnString != null) {
			LOG.debug("db conn string set to " + dbConnString);
			conf = new Conf(dbConnString);	
		} else {
			LOG.debug("db conn string isnt set, using default");
			conf = new Conf();
		}
		sectionLocks = new EnumMap<DBSection, Lock>(DBSection.class);
		initSectionLocks();
	}
	
	public static synchronized DBManagerAPI getInstance() {
		if (null == dbManager) {
			LOG.info("Initializing FS DB Manager");
			dbManager = new FileSystemDBManager();
		}
		return dbManager;	
	}
	
	
	/*****************************  
	 *    General DB operations
	 *****************************/
	
	/* verify the DataBase structure. should call this method soon as possible **/
	public void validateDBStructure() throws Exception {
	    LOG.debug("validating DB structure");
	    dbStructureOperation(false, true);
	}
	
	public void close() throws Exception {
		
	}
	
	private void dbStructureOperation(boolean create, boolean validate) throws Exception {
	    try {	 
    	    Field[] fields = conf.getClass().getDeclaredFields();
    	    File file;
    	    for (Field f : fields) {
    	        String path = f.get(conf).toString();
    	        file = new File(path);
    	        if (conf.isDirAttr(f.getName())) {
    	        	if (create) {
    	        		file.mkdirs();
    	        	}
    	        } else if (conf.isFileAttr(f.getName())) {
    	        	if (create) {
    	        		file.createNewFile();
    	        	}
    	        } else {
    	        	continue;
    	        }
    	        if (validate) {
    	        	LOG.debug("verifying " + path);    	           	     
    	        	assertPathExists(path);
    	        }   	  
    	    }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        LOG.error("Couldnt verify DB structure. caught " + e.getMessage());
	        throw e;
	    }
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
	
	private void lockQuoteDB() {
	    LOG.debug(String.format("QUOTE_DB LOCK (%d)", lockCount));
	    sectionLocks.get(DBSection.QUOTE_DB).lock();
	    lockCount++;
	}
	
	private void unlockQuoteDB() {
	    LOG.debug(String.format("QUOTE_DB UNLOCK (%d)", lockCount));
	    sectionLocks.get(DBSection.QUOTE_DB).lock();
	    lockCount--;
	}
	

	
	/*****************************  
	 *    Stocks DB operations
	 *****************************/
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#getStockCollection()
	 */
	
	@Override
	public StockCollection getStockCollection() throws IOException {
	    LOG.debug("getStockCollection");
		StockCollection stockCollection = new StockCollection();
		List<String[]> csvLines = readFromFileWithHeader(conf.stockListFile);
		LOG.debug("read " + csvLines.size() + " lines from file");
		for (String[] csvLine : csvLines) {
			stockCollection.add((new Stock()).initFromCSV(csvLine));
		}
		return stockCollection;
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#setStockCollection(org.gitprof.alcolil.common.AStockCollection)
	 */
	@Override
	public void setStockCollection(StockCollection stocks) throws IOException {
	    LOG.debug("setStockCollection");
	    List<String[]> csvLines = new ArrayList<String[]>();
	    csvLines.add(conf.stockFileHeader);
	    for (String symbol : stocks.getSymbols()) {
	    	try {
	    		String[] csvLine = stocks.getStock(symbol).convertToCSV();
	    		csvLines.add(csvLine);
	    	} catch (Exception e) {
	    		LOG.warn("didnt found symbol!  continuing...");
	    	}
	    }
	    writeToFile(conf.stockListFile, csvLines, false);
	}
	
	
	/*****************************  
	 *    Quotes DB operations
	 *****************************/
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#readFromQuoteDB(java.util.List, org.gitprof.alcolil.common.AInterval, org.gitprof.alcolil.common.ATime, org.gitprof.alcolil.common.ATime)
	 */
	@Override
	public StockSeries readFromQuoteDB(List<String> symbols, Interval interval) throws IOException {
	    lockQuoteDB();	    
		StockSeries stockSeries = new StockSeries(interval);
		TimeSeries timeSeries;
		BarSeries barSeries;
		for (String symbol : symbols) {
			timeSeries = readFromQuoteDB(symbol);
			barSeries = timeSeries.getBarSeries(interval);
			stockSeries.addBarSeries(symbol, barSeries);
		}
		unlockQuoteDB();
		return stockSeries;
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#rewriteToQuoteDB(org.gitprof.alcolil.common.AStockSeries)
	 */
	@Override
	public void rewriteToQuoteDB(StockSeries stockSeries) throws IOException {
		writeToQuoteDB(stockSeries, false);
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#appendToQuoteDB(org.gitprof.alcolil.common.AStockSeries)
	 */
	@Override
	public void appendToQuoteDB(StockSeries stockSeries) throws IOException {
		writeToQuoteDB(stockSeries, true);
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#readFromQuoteDB(java.lang.String, org.gitprof.alcolil.common.AInterval, org.gitprof.alcolil.common.ATime, org.gitprof.alcolil.common.ATime)
	 */
	@Override
	public TimeSeries readFromQuoteDB(String symbol) throws IOException {
	    lockQuoteDB();
	    LOG.info(String.format("reading timeSeries: symbol=%s.", symbol));
	    TimeSeries timeSeries = new TimeSeries(symbol);
	    List<File> intervalDirs = getQuotesIntervalDirs(getSymbolQuotesDirFullPath(symbol));
	    for (File intervalDir : intervalDirs) {
	    	Interval interval = getIntervalByFilePath(intervalDir.getPath());
	    	String symbolIntervalDir = getIntervalQuotesDirFullPath(symbol, interval);
	    	assertDirExists(symbolIntervalDir);  	
	    	BarSeries barSeries = readFromQuoteDB(symbol, interval);
			timeSeries.addBarSeries(interval, barSeries);
	    }
		unlockQuoteDB();
		return timeSeries;
	}
	
	public BarSeries readFromQuoteDB(String symbol, Interval interval) throws IOException {
		String symbolIntervalDir = getIntervalQuotesDirFullPath(symbol, interval);
    	List<File> files = getQuotesFilesByChronoOrder(symbolIntervalDir);
		List<String[]> csvLines;
		BarSeries barSeries = new BarSeries(symbol, interval);
		for (File child : files) {
			LOG.debug(String.format("reading quotes of symbol: %s. interval: %s. from file %s", symbol, interval, child.toString()));
		    csvLines = readFromFile(child.getPath());
			for (String[] csvLine : csvLines) {
				barSeries.addQuote((new Quote()).initFromCSV(csvLine));
			}
		}
		return barSeries;
	}
	
	public void rewriteToQuoteDB(BarSeries barSeries) throws IOException {
		writeToQuoteDB(barSeries, false);
	}
	
	public void appendToQuoteDB(BarSeries barSeries) throws IOException {
		writeToQuoteDB(barSeries, true);
	}
	
	private void writeToQuoteDB(StockSeries stockSeries, boolean append) throws IOException {
		for (String symbol : stockSeries.getSymbolList()) {
			writeToQuoteDB(stockSeries.getBarSeries(symbol), append);
		}
	}
	
	// TODO: handle unlocking in case of exception
	private void writeToQuoteDB(TimeSeries timeSeries, boolean append) throws IOException {
	    lockQuoteDB();
		String symbol = timeSeries.getSymbol();
		LOG.info("writing timeSeries for symbol: " + symbol);
		Path dirPath = Paths.get(conf.quoteDBDir, symbol);
		createDir(dirPath);
		BarSeries barSeries;
		for (Interval interval : Interval.values()) {
			barSeries = timeSeries.getBarSeries(interval);
			writeToQuoteDB(barSeries, append);	
		}
		unlockQuoteDB();
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#rewriteToQuoteDB(org.gitprof.alcolil.common.ATimeSeries)
	 */
	@Override
	public void rewriteToQuoteDB(TimeSeries timeSeries) throws IOException {
		writeToQuoteDB(timeSeries, false);
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#appendToQuoteDB(org.gitprof.alcolil.common.ATimeSeries)
	 */
	@Override
	public void appendToQuoteDB(TimeSeries timeSeries) throws IOException {
		writeToQuoteDB(timeSeries, true);
	}
	
	// TODO: handle unlocking in case of exception
	private void writeToQuoteDB(BarSeries barSeries, boolean append) throws IOException {
	    lockQuoteDB();	  
		String symbol = barSeries.getSymbol();
		Interval interval = barSeries.getInterval();
		List<Quote> quotes;
		List<String[]> csvLines = new ArrayList<String[]>();
		quotes = barSeries.getQuotes();
		for (Quote quote : quotes) {
			csvLines.add(quote.convertToCSV());
		}
		String dirpath = getIntervalQuotesDirFullPath(symbol, interval);
		createDir(Paths.get(dirpath));
		Path filepath = Paths.get(dirpath, tmpQuotesSingleFile);
		LOG.info(String.format("writing %d quotes for symbol: %s of interval %s to %s", csvLines.size(), symbol, interval.toString(), filepath.toString()));
		writeToFile(filepath.toString(), csvLines, append);	
		unlockQuoteDB();
	}

	private String getIntervalQuotesDirFullPath(String symbol, Interval interval) {
	    return Paths.get(getSymbolQuotesDirFullPath(symbol), interval.toString()).toString();  
	}
		
	private String getSymbolQuotesDirFullPath(String symbol) {
	    return Paths.get(conf.quoteDBDir, symbol).toString();  
	}
	
	private Interval getIntervalByFilePath(String pathname) throws IOException {
        Interval interval = null;
        if (pathname.contains(Interval.ONE_MIN.toString())) {
            interval = Interval.ONE_MIN;
        } else if (pathname.contains(Interval.FIVE_MIN.toString())) {
            interval = Interval.FIVE_MIN;
        } else if (pathname.contains(Interval.DAILY.toString())) {
            interval = Interval.DAILY;
        } else {
            throw new IOException();
        }
        return interval;
    }	
	
	private List<File> getQuotesIntervalDirs(String symbolDir) {
	    File dir = new File(symbolDir);
	    File[] subdirs = dir.listFiles();
	    List<File> subdirList = Arrays.asList(subdirs);
	    return subdirList;
	}
	
	private List<File> getQuotesFilesByChronoOrder(String symbolIntervalDir) {
	    File dir = new File(symbolIntervalDir);
	    File[] files = dir.listFiles();
	    List<File> fileList = Arrays.asList(files);
	   /* Collections.sort(fileList, new Comparator<File>() {
	        public int compare(File file1, File file2) {
	            Time time1 = getFileTime(file1);
	            Time time2 = getFileTime(file2);
	            return time1.before(time2) ? -1 :
	                   time2.before(time1) ?  1 :
	                   0;	            
	        }
	        
	        private Time getFileTime(File file) { 
	            String dateString = file.getName().split(".")[0].replaceAll("_", "_");
	            return new Time(dateString + "00:00:00.000 +0700");
	        }            
	    });*/
	    return fileList;
	}
	
	private String timeToFilename(Time time) {
	    return time.getDayDateString().replace("-", "_");
	}
	
	
	/*************************************
	******* Low level file operations *****
	****************************************/
	
	
	private enum DirOp {
		CREATE, DELETE
	}
	
	private enum FileOp {
        MODIFY, READ
    }
    
	private void createDir(Path path) {
		File folder = new File(path.toString());
		folder.mkdirs();
	}
	
	private void deleteDir(Path path) {
		File folder = new File(path.toString());
		folder.delete();
	}
	
	private void writeToFile(String pathname, List<String[]> lines, boolean append) throws IOException {
	    assertDirExists(Paths.get(pathname).getParent().toString());
	    for (String[] line : lines)
	    	for (String word : line)
	    		LOG.debug("DEBUGGGG: " + word);	
	    
		CSVWriter csvWriter = new CSVWriter(new FileWriter(pathname, append));
		csvWriter.writeAll(lines);
		csvWriter.close();
	}
	
	private List<String[]> readFromFileWithHeader(String pathname) throws IOException {
		return readFromFile(pathname, true);
	}
	
	private List<String[]> readFromFile(String pathname) throws IOException {
		return readFromFile(pathname, false);
	}
	
	private List<String[]> readFromFile(String pathname, boolean omitHeader) throws IOException {
	    LOG.debug("reading from file:" + pathname);
	    assertFileExists(pathname);
		CSVReader csvReader = new CSVReader(new FileReader(pathname));
		List<String[]> csvLines =  csvReader.readAll();
		assert csvLines != null : "problem while reading from csv file " + pathname;
		if (omitHeader)
			csvLines.remove(0);
		csvReader.close();
		return csvLines;
		
	}	
	
	private static void assertPathExists(String fullpath) {
        File f = new File(fullpath);
        assert f.exists() : "no path named " + fullpath;
    }
	
	private static void assertFileExists(String fullpath) {
	    File f = new File(fullpath);
	    assert (f.exists() && !f.isDirectory()) : "no file named " + fullpath;
	}
	
	private static void assertDirExists(String fullpath) {
	    File f = new File(fullpath);
        assert (f.exists() && f.isDirectory()) : "no directory named " + fullpath;
    }
}