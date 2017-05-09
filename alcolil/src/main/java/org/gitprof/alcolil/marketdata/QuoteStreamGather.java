package org.gitprof.alcolil.marketdata;

import org.gitprof.alcolil.common.*;

public class QuoteStreamGather implements Runnable{

	QuoteQueue quoteQueue;
	
	public QuoteStreamGather(QuoteQueue quoteQueue) {
		this.quoteQueue = quoteQueue;
	}

	@Override
	public void run() {
		// TODO
	}
}
