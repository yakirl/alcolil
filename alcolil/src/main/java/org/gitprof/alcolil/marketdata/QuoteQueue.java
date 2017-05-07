package org.gitprof.alcolil.marketdata;

import java.util.Queue;
import java.util.ArrayDeque;

import org.gitprof.alcolil.common.*;

/*
 * Just a Thread-safe implementation of ArrayDeuqe of Quotes
 */
public class QuoteQueue {

	Queue<AQuote> quotes;
	
	public QuoteQueue() {
		quotes = new ArrayDeque<AQuote>();
	}
	
	private enum QueueOp {
		PUSH, POP
	}
	
	public void push(AQuote quote) {
		quoteQueueAccess(QueueOp.PUSH, quote);
	}
	
	public AQuote pop() {
		return quoteQueueAccess(QueueOp.POP, null);
	}
	
	private synchronized AQuote quoteQueueAccess(QueueOp op, AQuote quote) {
		if (QueueOp.PUSH == op ) {
			push(quote);
		} else { // POP
			return quotes.poll();
		}
		return null;
	}
}
