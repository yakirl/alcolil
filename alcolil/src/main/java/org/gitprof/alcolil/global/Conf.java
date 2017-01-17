package org.gitprof.alcolil.global;

public class Conf {

	public static String baseDir = System.getProperty("usr.dir");
	public static String rootDB = baseDir + "database";
	public static String quoteDB = rootDB + "quotes";
	public static String stockDB = rootDB + "stocks";
	public static String TradeDB = rootDB + "trades";
	public static String logs = baseDir + "logs";
}
