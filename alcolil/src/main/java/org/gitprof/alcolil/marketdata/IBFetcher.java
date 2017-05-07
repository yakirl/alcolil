package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;


public class IBFetcher extends BaseFetcher {

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
	public void fetchHistoricalData(String symbol, GraphInterval graphInterval, ATime from, ATime to) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fetchRealTimeData(String symbol, GraphInterval graphInterval, ATime from) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ABarSeries getHistoricalData(String symbol, GraphInterval graphInterval, ATime from, ATime to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fetchHistoricalData(String symbol, AInterval graphInterval, ATime from, ATime to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchRealTimeData(String symbol, AInterval graphInterval, ATime from) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ABarSeries getHistoricalData(String symbol, AInterval graphInterval, ATime from, ATime to) {
		// TODO Auto-generated method stub
		return null;
	}

}
