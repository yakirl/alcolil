package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.*;

public class QuoteDataProcessor {
    private ATimeSeries timeSeries;
    
    public void addQuote(AQuote quote) {
        timeSeries.addQuote(quote);
    }
}
