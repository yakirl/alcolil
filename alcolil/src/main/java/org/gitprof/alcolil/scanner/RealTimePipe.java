package org.gitprof.alcolil.scanner;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.marketdata.BaseFetcher;

public class RealTimePipe extends BaseQuotePipe {

	ATime start;
	AStockCollection stocks;
	AInterval interval;
	
	public RealTimePipe(AStockCollection stocks, ATime from) {
		this.stocks = stocks;
		start = from;
		interval = AInterval.ONE_MIN;
	}
	
	private void startStreaming() {
		BaseFetcher fetcher = BaseFetcher.getDefaultFetcher();
		fetcher.connect();
		fetcher.activateStreaming(quoteQueue);
		for (String symbol : stocks.getSymbols()) {
			fetcher.postRealTimeJobLine(symbol, interval, start);
		}
		fetcher.deactivateStreaming();
		fetcher.disconnect();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startStreaming();
	}

}
