package org.gitprof.alcolil.scanner;

import java.util.List;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.marketdata.BaseFetcher;

public class RealTimePipe extends BaseQuotePipe {

	ATime start;
	List<String> symbols;
	AInterval interval;
	
	public RealTimePipe(List<String> symbols, ATime from) {
		this.symbols = symbols;
		start = from;
		interval = AInterval.ONE_MIN;
	}
	
	private void startStreaming() {
		BaseFetcher fetcher = BaseFetcher.getDefaultFetcher();
		fetcher.connect();
		fetcher.activateStreaming(quoteQueue);
		for (String symbol : symbols) {
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
