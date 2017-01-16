package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.ATimeSeries;

public abstract class BaseFetcher {

	public abstract void connect();
	
	public abstract void disconnect();
	
	public abstract ATimeSeries getHistory();
	
	public abstract void startRealTimeFetching();
		
	public abstract void StopRealTimeFetching();
	
}
