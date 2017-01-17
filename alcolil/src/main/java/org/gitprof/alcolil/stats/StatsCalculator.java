package org.gitprof.alcolil.stats;

import org.gitprof.alcolil.database.DBManager;

public class StatsCalculator {
	
	DBManager dbManager = null;
	
	public StatsCalculator() {
		dbManager = DBManager.getInstance();
	}
	
	public void calc() {
		
	}
}
