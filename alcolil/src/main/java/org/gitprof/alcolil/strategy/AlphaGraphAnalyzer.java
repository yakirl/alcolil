package org.gitprof.alcolil.strategy;


import java.math.*;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import yahoofinance.histquotes.HistoricalQuote;

import org.gitprof.alcolil.common.*;

public class AlphaGraphAnalyzer extends BaseGraphAnalyzer {

	private BarSeries barSeries;
	
	public void updateNextQuote(Quote quote) {
		//TODO
	}
	
	public AlphaGraphAnalyzer(BarSeries barSeries) {
		this.barSeries = barSeries;
	}
	
	public BigDecimal avgVolofXDays(int numDays, Time endTime) {
		return new BigDecimal(0);
	}
	
	public class RankedQuote implements Comparable<RankedQuote> {
		public Quote quote;
		public double rank;
		
		public RankedQuote(Quote quote, double rank) {
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
	
	private double calcSupportRank(ListIterator<Quote> bwdItr, Quote currQuote) {
		int D_MAX = 8;
		double rank = 0;
		//boolean isSupport = false;
		if ((!bwdItr.hasNext()) || (!bwdItr.hasPrevious())) {
			return 0;
		}
		ListIterator<Quote> fwdItr = (new ArrayList<Quote>()).listIterator(); // barSeries.listIterator(bwdItr.nextIndex()-1);
		bwdItr.next();
		//MathContext mc = new MathContext(2);
		int d; for (d = 0; d < D_MAX; d++) {
			if ((!fwdItr.hasNext()) || (!bwdItr.hasPrevious())) {
					break;
			}
			double curr = currQuote.low().doubleValue();
			double prev = bwdItr.previous().low().doubleValue();
			double next = fwdItr.next().low().doubleValue();
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
		int ix; for (ix = 0; ix < barSeries.size(); ix++) {
			//double supportRank = calcSupportRank(barSeries.listIterator(ix), barSeries.getQuote(ix));
			//RankedQuote rankedQuote = new RankedQuote(barSeries.getQuote(ix), supportRank);
			//supports.add(rankedQuote);
		}
		System.out.println("*******");
		for (ix = 0; ix < 5; ix++) {
			Quote quote = supports.poll().quote;
			System.out.println(quote.low());
			System.out.println(quote.time());
			System.out.println(supports.poll().rank);
		}
		System.out.println("*******");
	}
}
	
