package org.gitprof.alcolil.database;


public interface CSVable {

	public String[] convertToCSV();
	
	public CSVable initFromCSV(String[] csvs);
}
