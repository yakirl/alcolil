package org.gitprof.alcolil.common;

import org.gitprof.alcolil.database.CSVObject;
import org.gitprof.alcolil.database.CSVable;

public class AQuote implements CSVable {
	private String symbol;
	private APrice open;
	private APrice high;
	private APrice low;
	private APrice close;
	private Integer volume;
	private AInterval interval;
	private ATime time;
	
	public String getSymbol() {
		return symbol;
	}
	
	public CSVObject convertToCSV() {
		String[] fields = new String[8];
		fields[0] = symbol;
		fields[1] = open.toString();
		fields[2] = high.toString();
		fields[3] = low.toString();
		fields[4] = close.toString();
		fields[5] = volume.toString();
		fields[6] = interval.toString();
		fields[7] = time.toString();
		
		CSVObject csvObject = new CSVObject(fields);
		
		return csvObject;
	}
	
	public void initFromCSV(CSVObject csvObject) {
		symbol = csvObject.getField(0);
		open = new APrice(csvObject.getField(1));
		high = new APrice(csvObject.getField(2));
		low = new APrice(csvObject.getField(3));
		close = new APrice(csvObject.getField(4));
		volume = new Integer(csvObject.getField(5));
		interval = new AInterval(csvObject.getField(1));
		time = new ATime(csvObject.getField(1));
		
	}
}
