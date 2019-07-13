package org.gitprof.alcolil.common;

import java.math.BigDecimal;
import org.gitprof.alcolil.database.CSVable;

public class Quote implements CSVable {
	private String asymbol;
	private BigDecimal aopen;
	private BigDecimal ahigh;
	private BigDecimal alow;
	private BigDecimal aclose;
	private long avolume;
	private Interval ainterval;
	private Time atime;
	private boolean alive = false;
	private boolean eof = false;
	
	public Quote(String symbol,
			BigDecimal open,
			BigDecimal high,
			BigDecimal low,
			BigDecimal close,
			Integer volume,
			Interval interval,
			Time time) {
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
	
    public Quote(String symbol,
            double open,
            double high,
            double low,
            double close,
            long volume,
            Interval interval,
            Time time) {
        this.asymbol = symbol;
        this.aopen = BigDecimal.valueOf(open);
        this.ahigh = BigDecimal.valueOf(high);
        this.alow = BigDecimal.valueOf(low);
        this.aclose = BigDecimal.valueOf(close);
        this.avolume = volume;
        this.ainterval = interval;
        this.atime = time;
        this.alive = true;
    }
	   
	public Quote(Quote quote) {
		//TODO
	}
	
	// 'dead quote' builder
	public Quote() {
		
	}
	
	public Quote setEof() {
		eof = true;
		return this;
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
		
	public Interval interval() {
		return ainterval;
	}
	
	public Time time() {
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
	
	public void interval(Interval interval) {
		this.ainterval = interval;
	}
	
	public void time(Time time) {
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
	
	public Quote initFromCSV(String[] csvs) {
		asymbol = csvs[0];
		aopen = new BigDecimal(csvs[1]);
		ahigh = new BigDecimal(csvs[2]);
		alow = new BigDecimal(csvs[3]);
		aclose = new BigDecimal(csvs[4]);
		avolume = new Integer(csvs[5]);
		ainterval = Interval.valueOf(csvs[6]);
		atime = new Time(csvs[7]);
		return this;
	}
}
