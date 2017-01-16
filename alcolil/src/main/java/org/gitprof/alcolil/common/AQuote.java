package org.gitprof.alcolil.common;

import org.gitprof.alcolil.database.CSVObject;
import org.gitprof.alcolil.database.CSVable;

public class AQuote implements CSVable {
	private String symbol;
	private APrice open;
	private APrice high;
	private APrice low;
	private APrice close;
	private int volume;
	private AInterval interval;
	private ATime time;
	
	public CSVObject convertToCSV() {
		// TODO Auto-generated method stub
		return null;
	}
	public void initFromCSV(CSVObject csvObject) {
		// TODO Auto-generated method stub
		
	}
}
