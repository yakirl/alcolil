package org.yakirl.alcolil.strategy;

import org.yakirl.alcolil.common.*;

public class QuoteDataProcessor {
    private TimeSeries timeSeries;
    
    public void addQuote(Quote quote) {
        timeSeries.addQuote(quote);
    }
}
