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
//import java.util.Map;

public class ABarSeries implements Iterable<AQuote> {

    protected static final Logger LOG = LogManager.getLogger(ABarSeries.class);
	private String symbol;
	private AInterval interval;
	private List<AQuote> quotes;
	private Iterator<AQuote> quoteIterator;
	private Vector<QuoteQueue> observers;
	private QuoteQueue localObserver;
	private boolean doNotModify = false;
	
	public ABarSeries(AInterval interval) {
		this.interval = interval;
		quotes = new ArrayList<AQuote>();
		observers = new Vector<QuoteQueue>();
		localObserver = new QuoteQueue();
	}

	public ABarSeries(String symbol, AInterval interval) {
	    this.symbol = symbol;
        this.interval = interval;
        quotes = new ArrayList<AQuote>();
        observers = new Vector<QuoteQueue>();
        localObserver = new QuoteQueue();
    }
	
	public String getSymbol() {
		return symbol;
	}
	
	public AInterval getInterval() {
		return interval;
	}
	
	public List<AQuote> getQuotes() {
		return quotes;
	}
	
	public void addQuote(AQuote quote) {
		assert doNotModify == false : "Error! attempt to modify BarSeries while under iteration!";
		quotes.add(quote);
		localObserver.push(quote);
		if (observers.size() != 0) {
			notifyObservers(quote);
		}
	}
	
	public AQuote nextQuote() {
		return localObserver.pop();
	}
	
	public int size() {
		return quotes.size();
	}
	
	public AQuote getQuote(int ix) {
		return quotes.get(ix);
	}
	
	void addObserver() {
		observers.addElement(new QuoteQueue());
	}
	
	void notifyObservers(AQuote quote) {		
		for (QuoteQueue observer : observers) {
			observer.push(quote);
		}
	}

	@Override
	public Iterator<AQuote> iterator() {
		doNotModify = true;
		return quotes.listIterator();
	}
	
	public ATime getStartTime() {
		return quotes.get(0).time();
	}
	
	public ATime getEndTime() {
		return quotes.get(size() - 1).time();
	}
	
	/*
	 * support 1 minute only !
	 * Merge 2 barSeries (of the same bar interval)
	 * if there is a hole between them - fill with dead quotes
	 */
	public static ABarSeries mergeBarSeries(ABarSeries barSeries1, ABarSeries barSeries2) {
		//TODO: handle empty lists
		//if (barSeries1.isEmpty())
		//	ArrayList<AQuote> abc = (ArrayList<AQuote>) barSeries2.clone();
	    assert barSeries1.getSymbol() == barSeries2.getSymbol() : "attempt to merge barSeries with unmatched symbols!";
	    assert barSeries1.getInterval() == barSeries2.getInterval() : "attempt to merge barSeries with unmatched interval!";
		ATime start1 = barSeries1.getStartTime();
		ATime start2 = barSeries2.getStartTime();
		ATime end1   = barSeries1.getEndTime();
		ATime end2   = barSeries2.getEndTime();
		ABarSeries first, second, newBarSeries = new ABarSeries(barSeries1.getSymbol(), AInterval.ONE_MIN);
		first  = start1.before(start2) ? barSeries1 : barSeries2;
		second = start1.before(start2) ? barSeries2 : barSeries1;
		
		ATime startSecond = start1.before(start2) ? start2 : start1;
		ATime endSecond   = start1.before(start2) ? end2   :   end1;
		ATime endFirst    = start1.before(start2) ? end1   :   end2;
		
		for (AQuote quote : first) {
			newBarSeries.addQuote(quote);
		}
		ATime nextTime = ATime.addMinute(endFirst);
		// if we havent gone to the end of the secondBarSeries:
		if (nextTime.before(endSecond)) {
			// fill hole with dead quotes
			while (nextTime.before(startSecond)) {
				//add dead quotes
			    LOG.warn("found empty place while merging - inserting dead quote!");
				newBarSeries.addQuote(new AQuote());
				nextTime = ATime.addMinute(nextTime);
			} 
			// fill the rest with the second series
			for (AQuote quote : second) {
				newBarSeries.addQuote(quote);
			}
		}
		return newBarSeries;
	}


}
