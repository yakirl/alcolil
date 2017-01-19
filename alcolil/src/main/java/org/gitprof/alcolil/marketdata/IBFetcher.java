package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;


public class IBFetcher extends BaseFetcher {

	public IBFetcher(QuoteQueue quoteQueue) {
		this.quoteQueue = quoteQueue;
	}
	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ATimeSeries getHistory(String symbol, Enums.GraphInterval interval, ATime from, ATime to) {
		
		ATimeSeries aTimeSeries = new ATimeSeries();
		return aTimeSeries;
	}

	@Override
	public void startRealTimeFetching(String symbol, Enums.GraphInterval graphInterval, ATime from) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StopRealTimeFetching(String symbol, Enums.GraphInterval graphInterval) {
		// TODO Auto-generated method stub
		
	}

}
