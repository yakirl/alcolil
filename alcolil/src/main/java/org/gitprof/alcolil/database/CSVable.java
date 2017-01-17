package org.gitprof.alcolil.database;


public interface CSVable {

	public String[] convertToCSV();
	
	public void initFromCSV(String[] csvs);
}
