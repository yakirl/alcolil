package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.*;

public class QuoteDataProcessor {
    private TimeSeries timeSeries;
    
    public void addQuote(Quote quote) {
        timeSeries.addQuote(quote);
    }
}
