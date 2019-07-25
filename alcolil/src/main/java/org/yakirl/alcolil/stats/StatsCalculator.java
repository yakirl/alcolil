package org.yakirl.alcolil.stats;

import org.yakirl.alcolil.database.DBManagerAPI;
import org.yakirl.alcolil.database.FileSystemDBManager;

public class StatsCalculator {
	
	DBManagerAPI dbManager = null;
	
	public StatsCalculator() {
		dbManager = FileSystemDBManager.getInstance();
	}
	
	public void calc() {
		
	}
}
