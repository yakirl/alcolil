package org.yakirl.alcolil.database;


public interface CSVable {

	public String[] convertToCSV();
	
	public CSVable initFromCSV(String[] csvs);
}
