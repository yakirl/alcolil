package org.yakirl.alcolil.marketdata;

import org.yakirl.alcolil.common.*;


public class IBFetcher implements FetcherAPI {

	public IBFetcher() {
		//this.quoteQueue = quoteQueue;
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
	public void activateStreaming(QuoteQueue quoteQueue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deactivateStreaming() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void postHistoricalDataJobLine(String symbol, Interval interval, Time from, Time to) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void postRealTimeJobLine(String symbol, Interval interval, Time from) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public BarSeries getHistoricalData(String symbol, Interval interval, Time from, Time to) {
		// TODO Auto-generated method stub
		return null;
	}

}
