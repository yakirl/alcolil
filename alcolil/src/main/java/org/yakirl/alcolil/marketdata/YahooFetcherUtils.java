package org.yakirl.alcolil.marketdata;

import org.yakirl.alcolil.common.Interval;

/****  Format description ***
 * 
 * @author yakir
 *
 * There are specific valid ranges and interval.
 *  format of interval and range is: <num><units>
 *  units for range: d/mo/y
 *  units for interval: m/h/d/wk/mo
 *  URL for example: https://query1.finance.yahoo.com/v7/finance/chart/goog?range=3mo&interval=1d&indicators=quote&includeTimestamps=true
 *  
 *  Interval and rage limitation for Yahoo finance:
 *  	
 *  ----------------------------------------------------|
 *   interval      range                                |
 *     1d        1d, 5d, 1mo, 3mo, 6mo, 1y, 2y, 5y, max |
 *     1m        1d, 5d  (no max!!!)                    |
 *  ----------------------------------------------------|   	
 *  
 */


public class YahooFetcherUtils {

	protected String QUOTES_URL_PATTERN = "https://query1.finance.yahoo.com/v7/finance/chart/TICKER?range=RANGE&interval=INTERVAL&indicators=quote&includeTimestamps=true";
			
	// return URL for the given interval for max range
	String getQuotesUrl(String symbol, Interval interval) {
		String intervalUnits = null;
		int intervalLength = 0;
		int rangeDays = 0;
		switch (interval) {
		case ONE_MIN:
			intervalUnits = "m";
			intervalLength = 1;
			rangeDays = 1;
			break;
		case FIVE_MIN:
			intervalUnits = "m";
			intervalLength = 5;
			rangeDays = 5;
			break;
		case DAILY:
			intervalUnits = "d";
			intervalLength = 1;
			break;
		default:
			assert false : "Got unexpected time interval for yahoo quotes URL";
		}
		return getQuotesUrl(symbol, intervalUnits, intervalLength, rangeDays);
	}

	// currently supports only daily interval
	String getQuotesUrl(String symbol, String intervalUnits, int intervalLength, int rangeDays) {
		// assert interval == AInterval.DAILY : "currently only daily inerval is supported for yahoo Quotes URL";
		String duration;
		if (rangeDays == 0) {
			duration = "max";
		} else {
			duration = String.valueOf(rangeDays) + "d";	
		}
		String intervalStr = String.valueOf(intervalLength) + intervalUnits;
	    return (QUOTES_URL_PATTERN.replaceAll("RANGE", duration)).replaceAll("TICKER", symbol).replaceAll("INTERVAL", intervalStr);
	}
	
}
