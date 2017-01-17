package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;


public abstract class BaseFetcher {

	public abstract void connect();
	
	public abstract void disconnect();
	
	public abstract ATimeSeries getHistory(String symbol, AInterval inerval, ATime from, ATime to);
	
	public abstract void startRealTimeFetching();
		
	public abstract void StopRealTimeFetching();
	
}
