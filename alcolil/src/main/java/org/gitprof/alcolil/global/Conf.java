package org.gitprof.alcolil.global;

import java.nio.file.Paths;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Conf {

	// TODO: path connecting
	public static String rootDir  = getCWD();
	public static String DBDir    = Paths.get(rootDir, "database").toString();
	public static String logsDir  = Paths.get(rootDir, "logs").toString();
	public static String confsDir = Paths.get(rootDir, "conf").toString();
	public static String quoteDB  = Paths.get(DBDir, "quotes").toString();
	public static String stockDB  = Paths.get(DBDir, "stocks").toString();
	public static String TradeDB  = Paths.get(DBDir, "trades").toString();
	public static String statsDB  = Paths.get(DBDir, "stats").toString();
	// public static String yahooQuoteDB = Paths.get(quoteDB, "yahoo").toString(); 
	public static String backtestConfFile = Paths.get(confsDir, "backtester.properties").toString();
	public static String stockFilterConfFile = Paths.get(confsDir, "stockfilter.properties").toString();
	public static String analyzerConfFile = Paths.get(confsDir, "analyzer.properties").toString();
	
	public static String stockListFile = Paths.get(stockDB, "stocks_list.csv").toString(); 
	
	public static String srcDir = Paths.get(rootDir, "src").toString();
	
	// TODO: separate test conf to different module
	public static String testDir = Paths.get(srcDir, "test").toString();
	public static String testResourcesDir = Paths.get(testDir, "resources").toString();
	//public static String stockFile = 
	
	private static String getCWD() {
	    //String dir = System.getProperty("usr.dir");
	    String cwd = Paths.get("").toAbsolutePath().toString();
	    LogManager.getLogger(Conf.class).info("current working directory: " + cwd);
	    return cwd;	    
	}
	
	public static String appendToStockDB(String... subpath) {	    
	    return append(stockDB, subpath);
	}
	
	public static String appendToQuoteDB(String... subpath) {        
        return append(quoteDB, subpath);
    }
	
	public static String appendToTestResources(String... subpath) {	    
	    return append(testResourcesDir, subpath);
	}	
	
	public static String appendToConfsDir(String... subpath) {      
        return append(confsDir, subpath);
    }   
	
	private static String append(String dir, String... subpath) {
	    return Paths.get(dir, subpath).toString();
	}
}
