package org.gitprof.alcolil.strategy;


import java.math.*;
import java.lang.Math;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;

import yahoofinance.histquotes.HistoricalQuote;

public class AlphaGraphAnalyzer extends BaseGraphAnalyzer {

	private List<HistoricalQuote> quotes;
	
	public class RankedQuote implements Comparable<RankedQuote> {
		public HistoricalQuote quote;
		public double rank;
		
		public RankedQuote(HistoricalQuote quote, double rank) {
			this.quote = quote;
			this.rank = rank;
		}
		
		@Override
		public int compareTo(RankedQuote rankedQuote) {
			int ret = 0;
			if (this.rank < rankedQuote.rank) {
				ret = 1;
			} else {
				ret = -1;
			}
			return ret;
		}
	}
	
	public AlphaGraphAnalyzer(List<HistoricalQuote> quotes) {
		this.quotes = quotes;
	}
	
	private double calcSupportRank(ListIterator<HistoricalQuote> bwdItr, HistoricalQuote currQuote) {
		int D_MAX = 8;
		double rank = 0;
		//boolean isSupport = false;
		if ((!bwdItr.hasNext()) || (!bwdItr.hasPrevious())) {
			return 0;
		}
		ListIterator<HistoricalQuote> fwdItr = quotes.listIterator(bwdItr.nextIndex()-1);
		bwdItr.next();
		//MathContext mc = new MathContext(2);
		int d; for (d = 0; d < D_MAX; d++) {
			if ((!fwdItr.hasNext()) || (!bwdItr.hasPrevious())) {
					break;
			}
			double curr = currQuote.getLow().doubleValue();
			double prev = bwdItr.previous().getLow().doubleValue();
			double next = fwdItr.next().getLow().doubleValue();
			double leftDis  = prev - curr;
			double rightDis = next - curr;
			if (leftDis < 0 || rightDis < 0) {
				break;
			}
			double p = d;
			rank += Math.pow(leftDis, 2-(p/10)) + Math.pow(rightDis, 2-(p/10)); 
		}
		return d <= 1 ? 0 : rank;
	}
	
	public void findSupports() {
		PriorityQueue<RankedQuote> supports = new PriorityQueue<RankedQuote>();
		int ix; for (ix = 0; ix < quotes.size(); ix++) {
			double supportRank = calcSupportRank(quotes.listIterator(ix), quotes.get(ix));
			RankedQuote rankedQuote = new RankedQuote(quotes.get(ix), supportRank);
			supports.add(rankedQuote);
		}
		System.out.println("*******");
		for (ix = 0; ix < 5; ix++) {
			HistoricalQuote quote = supports.poll().quote;
			System.out.println(quote.getLow());
			System.out.println(quote.getDate().getTime());
			System.out.println(supports.poll().rank);
		}
		System.out.println("*******");
	}
}
	
