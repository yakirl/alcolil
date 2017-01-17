package org.gitprof.alcolil.scanner;

import org.gitprof.alcolil.common.AQuote;
import org.gitprof.alcolil.marketdata.BaseFetcher;
import org.gitprof.alcolil.marketdata.QuoteQueue;

public abstract class BaseQuotePipe implements Runnable {

	BaseFetcher fetcher;
	QuoteQueue quoteQueue;
	
	public void setMarketDataFetcher() {
		fetcher = BaseFetcher.getDefaultFetcher();
	}
	
	public BaseQuotePipe() {
		setMarketDataFetcher();
	}
	
	private void startQuoteStreaming() {
		fetcher.connect();
		fetcher.startRealTimeFetching();
	}
	
	public void run() {
		startQuoteStreaming();
	}
	
	public void quoteQueuePush(AQuote quote) {
		quoteQueue.push(quote);
	}
	
	public AQuote quoteQueuePoll(int waitForXSec) {
		AQuote quote;
		int sleepTime = 500; // millis
		int iterations = 0;
		while(true) {
			quote = quoteQueue.pop();
			if (null != quote)
				break;
			++iterations;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (((1000 / sleepTime) * iterations) >= waitForXSec)
				break;
			
		}
		
		return quote;
	}
	
	public abstract AQuote getNextQuote();
}
