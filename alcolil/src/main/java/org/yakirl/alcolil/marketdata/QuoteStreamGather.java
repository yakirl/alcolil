package org.yakirl.alcolil.marketdata;

import org.yakirl.alcolil.common.*;

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
