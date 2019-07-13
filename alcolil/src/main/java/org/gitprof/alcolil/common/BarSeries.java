package org.gitprof.alcolil.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.gitprof.alcolil.marketdata.QuoteQueue;

/*
 *  Bar Series
 *  this class is the basic class that models the quote series and used by most of the components
 *  it holds the quotes of a stock of a specific interval, and operates as on-the-fly iteration via
 *  one local observer and multi user-registered observers
 *  the class operates in one of 2 modes:
 *  1. insertion mode: 
 *   a. addQuote() quotes are inserted one by one into a queue:
 *   	- goes to local observer and user-registered observers
 *      - inserted to quote list
 *   b. nextQuote() return the oldest quote in the queue, from the local observer
 *  2. iteration mode:
 *   a. iterates the series from begin to end.
 *   b. once it turns to iteration mode it becomes immutable
 *  
 */

public class BarSeries implements Iterable<Quote> {

    protected static final Logger LOG = LogManager.getLogger(BarSeries.class);
	private String symbol;
	private Interval interval;
	private List<Quote> quotes;
	private Iterator<Quote> quoteIterator;	
	private QuoteQueue localObserver;
	private boolean doNotModify = false;
	

	
	public BarSeries(Interval interval) {
		this.interval = interval;
		quotes = new ArrayList<Quote>();		
		localObserver = new QuoteQueue();
	}

	public BarSeries(String symbol, Interval interval) {
	    this.symbol = symbol;
        this.interval = interval;
        quotes = new ArrayList<Quote>();        
        localObserver = new QuoteQueue();
    }
	
	public String getSymbol() {
		return symbol;
	}
	
	public Interval getInterval() {
		return interval;
	}
	
	public List<Quote> getQuotes() {
		return quotes;
	}
	
	public void addQuote(Quote quote) {
		assert doNotModify == false : "Error! attempt to modify BarSeries while under iteration!";
		quotes.add(quote);
		localObserver.push(quote);		
	}
	
	public Quote nextQuote() {
		return localObserver.pop();
	}
	
	public int size() {
		return quotes.size();
	}
	
	// TODO: this method is used only by UTs, remove it and handle inside UTs
	public Quote getQuote(int ix) {
		return quotes.get(ix);
	}		
	
	@Override
	public Iterator<Quote> iterator() {
		doNotModify = true;
		return quotes.listIterator();
	}
	
	public Time getStartTime() {
		return quotes.get(0).time();
	}
	
	public Time getEndTime() {
		return quotes.get(size() - 1).time();
	}
	
	/*
	 * support 1 minute only !
	 * Merge 2 barSeries (of the same bar interval)
	 * if there is a hole between them - fill with dead quotes
	 */
	public static BarSeries mergeBarSeries(BarSeries barSeries1, BarSeries barSeries2) {
		//TODO: handle empty lists
		//if (barSeries1.isEmpty())
		//	ArrayList<AQuote> abc = (ArrayList<AQuote>) barSeries2.clone();
	    assert barSeries1.getSymbol() == barSeries2.getSymbol() : "attempt to merge barSeries with unmatched symbols!";
	    assert barSeries1.getInterval() == barSeries2.getInterval() : "attempt to merge barSeries with unmatched interval!";
		Time start1 = barSeries1.getStartTime();
		Time start2 = barSeries2.getStartTime();
		Time end1   = barSeries1.getEndTime();
		Time end2   = barSeries2.getEndTime();
		BarSeries first, second, newBarSeries = new BarSeries(barSeries1.getSymbol(), Interval.ONE_MIN);
		first  = start1.before(start2) ? barSeries1 : barSeries2;
		second = start1.before(start2) ? barSeries2 : barSeries1;
		
		Time startSecond = start1.before(start2) ? start2 : start1;
		Time endSecond   = start1.before(start2) ? end2   :   end1;
		Time endFirst    = start1.before(start2) ? end1   :   end2;
		
		for (Quote quote : first) {
			newBarSeries.addQuote(quote);
		}
		Time nextTime = Time.addMinute(endFirst);
		// if we havent gone to the end of the secondBarSeries:
		if (nextTime.before(endSecond)) {
			// fill hole with dead quotes
			while (nextTime.before(startSecond)) {
				//add dead quotes
			    LOG.warn("found empty place while merging - inserting dead quote!");
				newBarSeries.addQuote(new Quote());
				nextTime = Time.addMinute(nextTime);
			} 
			// fill the rest with the second series
			for (Quote quote : second) {
				newBarSeries.addQuote(quote);
			}
		}
		return newBarSeries;
	}


}
