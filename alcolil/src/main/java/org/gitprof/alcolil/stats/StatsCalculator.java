package org.gitprof.alcolil.stats;

import org.gitprof.alcolil.database.DBManagerAPI;
import org.gitprof.alcolil.database.FileSystemDBManager;

public class StatsCalculator {
	
	DBManagerAPI dbManager = null;
	
	public StatsCalculator() {
		dbManager = FileSystemDBManager.getInstance();
	}
	
	public void calc() {
		
	}
}
