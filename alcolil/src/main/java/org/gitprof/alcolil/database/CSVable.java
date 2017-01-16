package org.gitprof.alcolil.database;


public interface CSVable {

	public CSVObject convertToCSV();
	
	public void initFromCSV(CSVObject csvObject);
}
