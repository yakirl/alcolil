package org.gitprof.alcolil.global;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Conf {

	// TODO: path connecting
	public static String rootDir  = System.getProperty("usr.dir");
	public static String DBDir    = Paths.get(rootDir, "database").toString();
	public static String logsDir  = Paths.get(rootDir, "logs").toString();
	public static String confsDir = Paths.get(rootDir, "conf").toString();
	public static String quoteDB  = Paths.get(DBDir, "quotes").toString();
	public static String stockDB  = Paths.get(DBDir, "stocks").toString();
	public static String TradeDB  = Paths.get(DBDir, "trades").toString();
	public static String statsDB  = Paths.get(DBDir, "stats").toString();
	public static String yahooQuoteDB = Paths.get(quoteDB, "yahoo").toString(); 
	
	public static String backtestFile = Paths.get(confsDir, "backtester.properties").toString();
	public static String stockFilterFile = Paths.get(confsDir, "stockfilter.properties").toString();
	
	public static String stockListFile = Paths.get(stockDB, "stocks_list.csv").toString(); 
	
	//public static String stockFile = 
	
}
