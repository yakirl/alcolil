package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.AInterval;

public class YahooFetcherUtils {

	private static String ONE_MIN_QUOTES_URL_PATTERN = "https://query1.finance.yahoo.com/v7/finance/chart/TICKER?range=RANGE&interval=INTERVAL&indicators=quote&includeTimestamps=true";
			
	String getQuotesUrl(String symbol, AInterval interval, int intervalLength) {
		return getQuotesUrl(symbol, interval, intervalLength, 0);
	}
	
	String getQuotesUrl(String symbol, AInterval interval, int intervalLength, int rangeDays) {
		String duration;
		if (rangeDays == 0) {
			duration = "max";
		} else {
			duration = String.valueOf(rangeDays) + "d";	
		}
	    return (ONE_MIN_QUOTES_URL_PATTERN.replaceAll("RANGE", duration)).replaceAll("TICKER", symbol);
	}
	
}
