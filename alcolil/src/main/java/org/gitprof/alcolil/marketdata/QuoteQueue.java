package org.gitprof.alcolil.marketdata;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.gitprof.alcolil.common.*;

/*
 * Simple implementation for Queue of quotes that set max timeout waiting for quote to
 * 	be pushed. since we always assume that the producer should end the streaming with Quote.eof() set.
 */
public class QuoteQueue {

	private BlockingQueue<Quote> quotes;
	private long pollingTimeoutMillis;
	
	public class QuoteQueueException extends Exception {
		static final long serialVersionUID = 1103434535L;
		private String msg;
		
		public QuoteQueueException(String msg) {
			this.msg = msg;
		}
		
		public String getMessage() {
			return msg;
		}
	}
	
	public QuoteQueue(long pollingTimeoutMillis) {
		quotes = new LinkedBlockingQueue<Quote>();
		this.pollingTimeoutMillis = pollingTimeoutMillis;
	}

	public void push(Quote quote) {
		quotes.add(quote);
	}
	
	public Quote pop() throws QuoteQueueException {
		Quote quote;
		try {
			quote = quotes.poll(pollingTimeoutMillis, TimeUnit.MILLISECONDS);
		} catch(InterruptedException e) {
			throw new QuoteQueueException(e.getMessage());
		}
		if (quote == null) {
			throw new QuoteQueueException(String.format("queue empty for %d millis", pollingTimeoutMillis)); 
		}
		return quote;
	}
}