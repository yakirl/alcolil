package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.scanner.BaseQuotePipe;

/*
 * this Class maintaining the database and keep it updated
 * once in a while the user should operate this module in order to retrieve info from marketdata sources
 * since most of the sources allow limited data fetching (only recent data)
 */
public class HistoricalDataUpdater {

	private static String symbolFile;
	// we use pipe and not directly marketDataFetcher
	private BaseQuotePipe quotePipe;
	
	public HistoricalDataUpdater() {
		
	}
	
	public ATimeSeries getHistoricalData() {
		return null;
	}
	
	public void updateQuoteDB() {
		//quotePipe.getHistoricalData();
	}
}
