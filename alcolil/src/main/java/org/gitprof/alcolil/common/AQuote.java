package org.gitprof.alcolil.common;

import java.math.BigDecimal;
//import org.gitprof.alcolil.database.CSVObject;
import org.gitprof.alcolil.database.CSVable;

public class AQuote implements CSVable {
	private String asymbol;
	private BigDecimal aopen;
	private BigDecimal ahigh;
	private BigDecimal alow;
	private BigDecimal aclose;
	private long avolume;
	private AInterval ainterval;
	private ATime atime;
	private boolean alive = false;
	private boolean eof = false;
	
	public AQuote(String symbol,
			BigDecimal open,
			BigDecimal high,
			BigDecimal low,
			BigDecimal close,
			Integer volume,
			AInterval interval,
			ATime time) {
		this.asymbol = symbol;
		this.aopen = open;
		this.ahigh = high;
		this.alow = low;
		this.aclose = close;
		this.avolume = volume;
		this.ainterval = interval;
		this.atime = time;
		this.alive = true;
	}
	
    public AQuote(String symbol,
            double open,
            double high,
            double low,
            double close,
            long volume,
            AInterval interval,
            ATime time) {
        this.asymbol = symbol;
        this.aopen = new BigDecimal(open);
        this.ahigh = new BigDecimal(high);
        this.alow = new BigDecimal(low);
        this.aclose = new BigDecimal(close);
        this.avolume = volume;
        this.ainterval = interval;
        this.atime = time;
        this.alive = true;
    }
	   
	public AQuote(AQuote quote) {
		//TODO
	}
	
	// 'dead quote' builder
	public AQuote() {
		
	}
	
	public void setEof() {
		eof = true;
	}
	
	public boolean eof() {
		return eof;
	}
	
	public String symbol() {
		return asymbol;
	}
	
	public BigDecimal open() {
		return aopen;
	}
	
	public BigDecimal high() {
		return ahigh;
	}
	
	public BigDecimal low() {
		return alow;
	}
	
	public BigDecimal close() {
		return aclose;
	}
	
	public long volume() {
		return avolume;
	}
		
	public AInterval interval() {
		return ainterval;
	}
	
	public ATime time() {
		return atime;
	}
	
	public void open(BigDecimal open) {
		this.aopen = open;
	}
	
	public void high(BigDecimal high) {
		this.ahigh = high;
	}
	
	public void low(BigDecimal low) {
		this.alow = low;
	}
	
	public void close(BigDecimal close) {
		this.aclose = close;
	}
	
	public void volume(long volume) {
		this.avolume = volume;
	}
	
	public void interval(AInterval interval) {
		this.ainterval = interval;
	}
	
	public void time(ATime time) {
		this.atime = time;
	}
	
	public boolean isDead() {
		return !alive;
	}
	
	public String[] convertToCSV() {
		String[] fields = new String[8];
		fields[0] = asymbol;
		fields[1] = aopen.toString();		
		fields[2] = ahigh.toString();
		fields[3] = alow.toString();
		fields[4] = aclose.toString();
		fields[5] = String.valueOf(avolume);
		fields[6] = ainterval.toString();
		fields[7] = atime.toString();
		return fields;
	}
	
	public CSVable initFromCSV(String[] csvs) {
		asymbol = csvs[0];
		aopen = new BigDecimal(csvs[1]);
		ahigh = new BigDecimal(csvs[2]);
		alow = new BigDecimal(csvs[3]);
		aclose = new BigDecimal(csvs[4]);
		avolume = new Integer(csvs[5]);
		ainterval = AInterval.valueOf(csvs[6]);
		atime = new ATime(csvs[7]);
		return this;
	}
}
