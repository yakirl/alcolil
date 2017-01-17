package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;


public class IBFetcher extends BaseFetcher {

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
		// TODO Auto-generated method stub
		ATimeSeries aTimeSeries = new ATimeSeries();
		return aTimeSeries;
	}

	@Override
	public void startRealTimeFetching() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StopRealTimeFetching() {
		// TODO Auto-generated method stub
		
	}

}
