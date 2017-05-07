package org.gitprof.alcolil.common;

//import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/*
 * ------ date and time object ----------
 * 1. we are using the new joda-time package
 * 2. we will use this string format: yyyy-MM-dd HH:mm:ss.SSSZZ
 * 		SSS - milis
 * 		ZZ - timezone offset
 */
public class ATime {

	//Calendar calendar;
	DateTime dateTime;
	String pattern = "yyyy-MM-dd HH:mm:ss.SSSZZ";
	
	public ATime(String timeStr) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		dateTime = DateTime.parse(timeStr, fmt);
	}
	
	public ATime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	public boolean before(ATime time) {
		return dateTime.isBefore(time.dateTime);
	}
	
	public static ATime addMinute(ATime time) {
		return addXMinutes(time, 1);
	}
	
	public static ATime addXMinutes(ATime time, int mins) {
		return new ATime(time.dateTime.withFieldAdded(DurationFieldType.minutes(), mins));
	}
	
}
