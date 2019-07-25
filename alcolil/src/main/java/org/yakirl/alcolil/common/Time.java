package org.yakirl.alcolil.common;

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
public class Time {

	private DateTime dateTime;
	private static final String pattern = "yyyy-MM-dd HH:mm:ss.SSS Z";
	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern).withZone(getDefaultTimeZone());
	private static final String NEW_YORK_TIME_ZONE = "America/New_York";
	
	public Time(String timeStr) {	
		dateTime = DateTime.parse(timeStr, formatter);
	}
	
	/* seconds from the the epoch time: jan 1, 1970, 00:00:00 GMT */
	public Time(long unixTimestamp) {	       	    
        dateTime = new DateTime(unixTimestamp * 1000L);      
	}
	
	public Time(DateTime dateTime) {
		this.dateTime = dateTime;
	}	
	
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (obj == this) return true;
	    if (!(obj instanceof Time))return false;
	    Time time = (Time)obj;
	    System.out.println(String.format("%s. %s", time.getDateTime().toString(), dateTime.toString()));
	    return dateTime.isEqual(time.getDateTime());
	}
	
	public DateTime getDateTime() {
	    return dateTime;
	}
	
	private static DateTimeZone getDefaultTimeZone() {
	    return DateTimeZone.getProvider().getZone(NEW_YORK_TIME_ZONE);
	}
	
	public boolean before(Time time) {
		return dateTime.isBefore(time.dateTime);
	}
	
	/* get seconds since epoch */
	public long getSeconds() {
	    return dateTime.getMillis() / 1000L;
	}
	public static Time addMinute(Time time) {
		return addXMinutes(time, 1);
	}
	
	public static Time addXMinutes(Time time, int mins) {
		return new Time(time.dateTime.withFieldAdded(DurationFieldType.minutes(), mins));
	}
	
	public Time minusMinutes(int mins) {
	    return new Time(dateTime.minusMinutes(mins));
	}
	
	public Time addXWeeks(int weeks) {
	    return new Time(dateTime.plusWeeks(weeks));
	}
	
	public String getDayDateString() {
	    return null;
	}
	
	public Time firstDayOfWeek() {
	    return new Time(dateTime.dayOfWeek().getMinimumValue());
	}
	
	public static Time now() {
	    return new Time(DateTime.now());
	}
	
	public static long durationInSeconds(Time from, Time to) {
        return getDuration(from, to).getStandardSeconds();     
    }
	
	public static long durationInMinutes(Time from, Time to) {
        return getDuration(from, to).getStandardMinutes();     
    }
	
	public static long durationInHours(Time from, Time to) {
        return getDuration(from, to).getStandardHours();     
    }
	
	public static long durationInDays(Time from, Time to) {
	    return getDuration(from, to).getStandardDays();	    
	}
	
	private static Duration getDuration(Time from, Time to) {
	    return new Duration(from.dateTime, to.dateTime);
	}
	
	public Time roundToXMin(Interval interval) {
	    int minutes = 0;
	    if (interval == Interval.ONE_MIN)
	        minutes = 1;
	    else if (interval == Interval.FIVE_MIN)
	        minutes = 5;
	    else
	        assert false : "currently support 5 min or 1 min round";
	    return roundToXMin(minutes);
	}
	
	private Time roundToXMin(int minutes) {	    
        if (minutes < 1 || 60 % minutes != 0) {
            throw new IllegalArgumentException("minutes must be a factor of 60");
        }
        final DateTime hour = dateTime.hourOfDay().roundFloorCopy();
        final long millisSinceHour = new Duration(hour, dateTime).getMillis();
        final int roundedMinutes = ((int)Math.round(
            millisSinceHour / 60000.0 / minutes)) * minutes;
        if (((millisSinceHour * 1000) - roundedMinutes) < ((float)minutes / 2.0)) {
            return new Time(hour.minusMinutes(roundedMinutes));
        } else {
            return new Time(hour.plusMinutes(roundedMinutes));
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
