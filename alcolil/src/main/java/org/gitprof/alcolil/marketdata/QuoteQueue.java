package org.gitprof.alcolil.marketdata;

import java.util.Queue;
import java.util.ArrayDeque;

import org.gitprof.alcolil.common.*;

/*
 * Just a Thread-safe implementation of ArrayDeuqe of Quotes
 */
public class QuoteQueue {

	Queue<Quote> quotes;
	
	public QuoteQueue() {
		quotes = new ArrayDeque<Quote>();
	}

	public void push(Quote quote) {
		quoteQueueAccess(QueueOp.PUSH, quote);
	}
	
	public Quote pop() {
		return quoteQueueAccess(QueueOp.POP, null);
	}
	
	private enum QueueOp {
		PUSH, POP
	}
	
	private synchronized Quote quoteQueueAccess(QueueOp op, Quote quote) {
		if (QueueOp.PUSH == op ) {
			quotes.add(quote);
		} else { // POP
			return quotes.poll();
		}
		return null;
	}
}