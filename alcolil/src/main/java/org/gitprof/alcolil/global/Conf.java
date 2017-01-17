package org.gitprof.alcolil.global;

public class Conf {

	private static String baseDir = System.getProperty("usr.dir");
	private static String rootDB = baseDir + "database";
	private static String quoteDB = rootDB + "quotes";
	private static String stockDB = rootDB + "stocks";
	private static String TradeDB = rootDB + "trades";
	public static String logs = baseDir + "logs";
}
