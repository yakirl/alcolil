package org.gitprof.alcolil.common;

//import org.gitprof.alcolil.database.CSVObject;
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
	private boolean live = false;
	
	public AQuote(String symbol,
			APrice open,
			APrice high,
			APrice low,
			APrice close,
			Integer volume,
			AInterval interval,
			ATime time) {
		this.symbol = symbol;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.interval = interval;
		this.time = time;
		this.live = true;
	}
	
	// 'dead quote' builder
	public AQuote() {
		
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public ATime getTime() {
		return time;
	}
	
	public String[] convertToCSV() {
		String[] fields = new String[8];
		fields[0] = symbol;
		fields[1] = open.toString();
		fields[2] = high.toString();
		fields[3] = low.toString();
		fields[4] = close.toString();
		fields[5] = volume.toString();
		fields[6] = interval.toString();
		fields[7] = time.toString();
		
		//CSVObject csvObject = new CSVObject(fields);
		
		return fields;
	}
	
	public void initFromCSV(String[] csvs) {
		symbol = csvs[0];
		open = new APrice(csvs[1]);
		high = new APrice(csvs[2]);
		low = new APrice(csvs[3]);
		close = new APrice(csvs[4]);
		volume = new Integer(csvs[5]);
		interval = new AInterval(csvs[6]);
		time = new ATime(csvs[7]);
		
	}
}
