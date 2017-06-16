package org.gitprof.alcolil.common;

//import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import java.util.TimeZone;


/*
 * ------ date and time object ----------
 * 1. we are using the new joda-time package
 * 2. we will use this string format: yyyy-MM-dd HH:mm:ss.SSS Z
 * 		SSS - milis
 * 		ZZ - timezone offset
 * 3. NYSE time zone 
 */
public class ATime {

	//Calendar calendar;
	private DateTime dateTime;
	private static final String pattern = "yyyy-MM-dd HH:mm:ss.SSS Z";
	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern).withZone(getDefaultTimeZone());
	private static final String NEW_YORK_TIME_ZONE = "America/New_York";
	
	public ATime(String timeStr) {	
		dateTime = DateTime.parse(timeStr, formatter);
	}
	
	/* seconds from the the epoch time: jan 1, 1970, 00:00:00 GMT */
	public ATime(long unixTimestamp) {	       	    
        dateTime = new DateTime(unixTimestamp * 1000L);      
	}
	
	public ATime(DateTime dateTime) {
		this.dateTime = dateTime;
	}	
	
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (obj == this) return true;
	    if (!(obj instanceof ATime))return false;
	    ATime time = (ATime)obj;
	    System.out.println(String.format("%s. %s", time.getDateTime().toString(), dateTime.toString()));
	    return dateTime.isEqual(time.getDateTime());
	}
	
	public DateTime getDateTime() {
	    return dateTime;
	}
	
	private static DateTimeZone getDefaultTimeZone() {
	    return DateTimeZone.getProvider().getZone(NEW_YORK_TIME_ZONE);
	}
	
	public boolean before(ATime time) {
		return dateTime.isBefore(time.dateTime);
	}
	
	/* get seconds since epoch */
	public long getSeconds() {
	    return dateTime.getMillis() / 1000L;
	}
	public static ATime addMinute(ATime time) {
		return addXMinutes(time, 1);
	}
	
	public static ATime addXMinutes(ATime time, int mins) {
		return new ATime(time.dateTime.withFieldAdded(DurationFieldType.minutes(), mins));
	}
	
	public ATime minusMinutes(int mins) {
	    return new ATime(dateTime.minusMinutes(mins));
	}
	
	public ATime addXWeeks(int weeks) {
	    return new ATime(dateTime.plusWeeks(weeks));
	}
	
	public String getDayDateString() {
	    return null;
	}
	
	public ATime firstDayOfWeek() {
	    return new ATime(dateTime.dayOfWeek().getMinimumValue());
	}
	
	public static ATime now() {
	    return new ATime(DateTime.now());
	}
	
	public static long durationInSeconds(ATime from, ATime to) {
        return getDuration(from, to).getStandardSeconds();     
    }
	
	public static long durationInMinutes(ATime from, ATime to) {
        return getDuration(from, to).getStandardMinutes();     
    }
	
	public static long durationInHours(ATime from, ATime to) {
        return getDuration(from, to).getStandardHours();     
    }
	
	public static long durationInDays(ATime from, ATime to) {
	    return getDuration(from, to).getStandardDays();	    
	}
	
	private static Duration getDuration(ATime from, ATime to) {
	    return new Duration(from.dateTime, to.dateTime);
	}
	
	public ATime roundToXMin(AInterval interval) {
	    int minutes = 0;
	    if (interval == AInterval.ONE_MIN)
	        minutes = 1;
	    else if (interval == AInterval.FIVE_MIN)
	        minutes = 5;
	    else
	        assert false : "currently support 5 min or 1 min round";
	    return roundToXMin(minutes);
	}
	
	private ATime roundToXMin(int minutes) {	    
        if (minutes < 1 || 60 % minutes != 0) {
            throw new IllegalArgumentException("minutes must be a factor of 60");
        }
        final DateTime hour = dateTime.hourOfDay().roundFloorCopy();
        final long millisSinceHour = new Duration(hour, dateTime).getMillis();
        final int roundedMinutes = ((int)Math.round(
            millisSinceHour / 60000.0 / minutes)) * minutes;
        if (((millisSinceHour * 1000) - roundedMinutes) < ((float)minutes / 2.0)) {
            return new ATime(hour.minusMinutes(roundedMinutes));
        } else {
            return new ATime(hour.plusMinutes(roundedMinutes));
        }
    }
	
	/* print time with default time zone */
	public String formattedString() {
	    return dateTime.toString(formatter);
	}
	
	public String toString() {
	    return formattedString();
	}
	
}
