package org.gitprof.alcolil.global;

import java.nio.file.Paths;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
// import org.jfree.util.Log;

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
	
	public static String stockListFile = Paths.get(stockDB, "stocks_list.csv").toString(); 
	
	//public static String stockFile = 
	
	private static String getCWD() {
	    //String dir = System.getProperty("usr.dir");
	    String cwd = Paths.get("").toAbsolutePath().toString();
	    LogManager.getLogger(Conf.class).info("current working directory: " + cwd);
	    return cwd;	    
	}
	
	public static String appendToStockDB(String filename) {
	    return Paths.get(stockDB, filename).toString();
	}
	
	public static String appendToQuoteDB(String filename) {
        return Paths.get(quoteDB, filename).toString();
    }
}
