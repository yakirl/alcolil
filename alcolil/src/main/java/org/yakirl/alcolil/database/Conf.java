package org.yakirl.alcolil.database;

import java.nio.file.Paths;

/*********************
 * @author yakir
 * 
 *  DB Conf holds the info about the database structure.
 *  for FileSystem based DB this is the directories and files structure
 *
 */

public class Conf extends org.yakirl.alcolil.global.Conf {

	// TODO: path connecting
	public String rootDir;
	public String DBDir;
	public String logsDir;
	public String quoteDBDir;
	public String stockDBDir;
	public String tradeDBDir;
	public String statsDBDir;
	
	public String stockListFile; 
	public String[] stockFileHeader;
	
	// Constructor for default root DB
	public Conf() {
		init(root());
	}
	
	// Constructor for custom root DB
	public Conf(String dbConnString) {
		init(dbConnString);
	}
		
	private void init(String _rootDir) {
		rootDir = _rootDir;
		DBDir    = Paths.get(rootDir, "database").toString(); 
		logsDir  = Paths.get(rootDir, "logs").toString();       
		quoteDBDir  = Paths.get(DBDir, "quotes").toString();     
		stockDBDir  = Paths.get(DBDir, "stocks").toString();     
		tradeDBDir  = Paths.get(DBDir, "trades").toString();     
		statsDBDir  = Paths.get(DBDir, "stats").toString();
		stockListFile = Paths.get(stockDBDir, "stocks_list.csv").toString();
		stockFileHeader = new String[] {"Symbol", "Sector", "MarketCap", "LastPrice", "AvgVolOfXDays"};
	}
	
	public boolean isDirAttr(String attr) {
		return attr.length() >= 3 && (attr.substring(attr.length() - 3, attr.length()).equals("Dir"));
	}
	
	public boolean isFileAttr(String attr) {
		return attr.length() >= 4 && (attr.substring(attr.length() - 4, attr.length()).equals("File"));
	}
	
	public String appendToStockDB(String... subpath) {	    
	    return append(stockDBDir, subpath);
	}
	
	public String appendToQuoteDB(String... subpath) {        
        return append(quoteDBDir, subpath);
    } 
	
	private static String append(String dir, String... subpath) {
	    return Paths.get(dir, subpath).toString();
	}
}
