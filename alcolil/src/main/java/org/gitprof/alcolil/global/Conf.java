package org.gitprof.alcolil.global;

public class Conf {

	// TODO: path connecting
	public static String baseDir = System.getProperty("usr.dir");
	public static String rootDB   = baseDir + "database";
	public static String logsDir  = baseDir + "logs";
	public static String confsDir = baseDir + "conf";
	public static String quoteDB = rootDB + "quotes";
	public static String stockDB = rootDB + "stocks";
	public static String TradeDB = rootDB + "trades";
	public static String statsDB = rootDB + "stats";
	public static String yahooQuoteDB = quoteDB + "yahoo"; 
	
	public static String backtestFile = confsDir + "backtester.properties";
	public static String stockFilterFile = confsDir + "stockfilter.properties";
	
	//public static String stockFile = 
	
}
