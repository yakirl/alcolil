package org.gitprof.alcolil.database;

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
import org.apache.logging.log4j.LogManager;

import org.gitprof.alcolil.common.*;

/***********************
 * @author Yakir
 * 
 *  DBManager provides high level database operations
 *  Usage:
 *  this class should be used as singleton object with concurrent usage among multiple java threads
 *
 */

public class FileSystemDBManager implements DBManagerAPI {

	private static final String DB_CONN_STRING_ENV = "DB_CONN_STRING";
    protected static final Logger LOG = LogManager.getLogger(FileSystemDBManager.class);
	Map<DBSection, Lock> sectionLocks;	
	private static DBManagerAPI dbManager = null;
	private static int lockCount = 0;  // for debug
	protected Conf conf;
	
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
	
	public void dbStructureOperation(boolean create, boolean validate) throws Exception {
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
	
	/* verify the DataBase structure. should call this method soon as possible **/
	public void validateDBStructure() throws Exception {
	    LOG.debug("validating DB structure");
	    dbStructureOperation(false, true);
	}
	
	public void createDBStructure() throws Exception {
	    LOG.debug("creating DB structure");
	    dbStructureOperation(true, true);
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
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#getStockCollection()
	 */
	
	@Override
	public AStockCollection getStockCollection() throws IOException {
	    LOG.debug("getStockCollection");
		AStockCollection stockCollection = new AStockCollection();
		List<String[]> csvLines = readFromFile(conf.stockListFile);
		LOG.debug("read " + csvLines.size() + " lines from file");
		for (String[] csvLine : csvLines) {
			stockCollection.add((AStock)(new AStock()).initFromCSV(csvLine));
		}
		return stockCollection;
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#setStockCollection(org.gitprof.alcolil.common.AStockCollection)
	 */
	@Override
	public void setStockCollection(AStockCollection stocks) throws IOException {
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
	
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#readFromQuoteDB(java.util.List, org.gitprof.alcolil.common.AInterval)
	 */
	@Override
	public AStockSeries readFromQuoteDB(List<String> symbols, AInterval interval) throws IOException {
	    return null;
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#readFromQuoteDB(java.util.List)
	 */
	@Override
	public AStockSeries readFromQuoteDB(List<String> symbols) throws IOException {
	    return null;
	}		
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#readFromQuoteDB(java.util.List, org.gitprof.alcolil.common.AInterval, org.gitprof.alcolil.common.ATime, org.gitprof.alcolil.common.ATime)
	 */
	@Override
	public AStockSeries readFromQuoteDB(List<String> symbols, AInterval interval, ATime from, ATime to) throws IOException {
	    lockQuoteDB();	    
		AStockSeries stockSeries = new AStockSeries(interval);
		ATimeSeries timeSeries;
		ABarSeries barSeries;
		for (String symbol : symbols) {
			timeSeries = readFromQuoteDB(symbol, interval, from, to);
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
	public void rewriteToQuoteDB(AStockSeries stockSeries) throws IOException {
		writeToQuoteDB(stockSeries, false);
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#appendToQuoteDB(org.gitprof.alcolil.common.AStockSeries)
	 */
	@Override
	public void appendToQuoteDB(AStockSeries stockSeries) throws IOException {
		writeToQuoteDB(stockSeries, true);
	}
	
	// TODO: handle unlocking in case of excpetion
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#readFromQuoteDB(java.lang.String)
	 */
	@Override
	public ATimeSeries readFromQuoteDB(String symbol) throws IOException {
	    return null;
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#readFromQuoteDB(java.lang.String, org.gitprof.alcolil.common.AInterval, org.gitprof.alcolil.common.ATime, org.gitprof.alcolil.common.ATime)
	 */
	@Override
	public ATimeSeries readFromQuoteDB(String symbol, AInterval interval, ATime from, ATime to) throws IOException {
	    lockQuoteDB();
	    LOG.info(String.format("reading timeSeries: symbol=%s. interval=%s. from=%s. to=%s", 
                               symbol, interval.toString(), from.toString(), to.toString() ));
	    String symbolIntervalDir = getQuoteFileFullPath(symbol, interval);	    
	    assertDirExists(symbolIntervalDir);
	    from = from.firstDayOfWeek();
	    to   = to.  firstDayOfWeek();
		List<File> files = getQuotesFilesByChronoOrder(symbolIntervalDir);		
		List<String[]> csvLines;
		ATimeSeries timeSeries = new ATimeSeries(symbol);
		for (File child : files) {
			//getQuoteFileFullPath(symbol, interval);
			//String pathname = Paths.get(Conf.quoteDB, symbol, child.getName());
		    csvLines = readFromFile(child.getPath());
		    AInterval _interval = getIntervalByFilePath(child.getPath());
			ABarSeries barSeries = new ABarSeries(symbol, _interval);
			for (String[] csvLine : csvLines) {
				barSeries.addQuote((AQuote)((new AQuote()).initFromCSV(csvLine)));
			}
			timeSeries.addBarSeries(_interval, barSeries);
		} 
		unlockQuoteDB();
		return timeSeries;
	}
	
	private void writeToQuoteDB(AStockSeries stockSeries, boolean append) throws IOException {
		for (String symbol : stockSeries.getSymbolList()) {
			writeToQuoteDB(stockSeries.getBarSeries(symbol), append);
		}
	}
	
	// TODO: handle unlocking in case of exception
	private void writeToQuoteDB(ATimeSeries timeSeries, boolean append) throws IOException {
	    lockQuoteDB();
		String symbol = timeSeries.getSymbol();
		LOG.info("writing timeSeries for symbol: " + symbol);
		Path dirPath = Paths.get(conf.quoteDBDir, symbol);
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
		unlockQuoteDB();
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#rewriteToQuoteDB(org.gitprof.alcolil.common.ATimeSeries)
	 */
	@Override
	public void rewriteToQuoteDB(ATimeSeries timeSeries) throws IOException {
		writeToQuoteDB(timeSeries, false);
	}
	
	/* (non-Javadoc)
	 * @see org.gitprof.alcolil.database.DBManagerAPI#appendToQuoteDB(org.gitprof.alcolil.common.ATimeSeries)
	 */
	@Override
	public void appendToQuoteDB(ATimeSeries timeSeries) throws IOException {
		writeToQuoteDB(timeSeries, true);
	}
	
	// TODO: handle unlocking in case of exception
	private void writeToQuoteDB(ABarSeries barSeries, boolean append) throws IOException {
	    lockQuoteDB();	  
		String symbol = barSeries.getSymbol();
		LOG.info("writing timeSeries for symbol: " + symbol);
		AInterval interval = barSeries.getInterval();
		Path dirPath = Paths.get(conf.quoteDBDir + "\\" + symbol);
		createDir(dirPath);
		List<AQuote> quotes;
		List<String[]> csvLines = new ArrayList<String[]>();
		quotes = barSeries.getQuotes();
		for (AQuote quote : quotes) {
			csvLines.add(quote.convertToCSV());
		}
		String pathname = getQuoteFileFullPath(symbol, interval);
		writeToFile(pathname, csvLines, append);	
		unlockQuoteDB();
	}

	private String getQuoteFileFullPath(String symbol, AInterval interval) {
	    return Paths.get(conf.quoteDBDir, symbol, symbol + "_" + interval.toString()).toString();  
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
	
	private List<File> getQuotesFilesByChronoOrder(String symbolIntervalDir) {
	    File dir = new File(symbolIntervalDir);
	    File[] files = dir.listFiles();
	    List<File> fileList = Arrays.asList(files);
	    Collections.sort(fileList, new Comparator<File>() {
	        public int compare(File file1, File file2) {
	            ATime time1 = getFileTime(file1);
	            ATime time2 = getFileTime(file2);
	            return time1.before(time2) ? -1 :
	                   time2.before(time1) ?  1 :
	                   0;	            
	        }
	        
	        private ATime getFileTime(File file) { 
	            String dateString = file.getName().split(".")[0].replaceAll("_", "_");
	            return new ATime(dateString + "00:00:00.000 +0700");
	        }            
	    });
	    return fileList;
	}
	
	private String timeToFilename(ATime time) {
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
		CSVWriter csvWriter = new CSVWriter(new FileWriter(pathname, append));
		csvWriter.writeAll(lines);
		csvWriter.close();
	}
	
	private List<String[]> readFromFile(String pathname) throws IOException {
	    LOG.debug("reading from file:" + pathname);
	    assertFileExists(pathname);
		CSVReader csvReader = new CSVReader(new FileReader(pathname));
		List<String[]> csvLines =  csvReader.readAll();
		assert csvLines != null : "problem while reading from csv file " + pathname;
		assert csvLines.size() > 0 : "empty CSV file: " + pathname;
		csvLines.remove(0); // removes csv title
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
